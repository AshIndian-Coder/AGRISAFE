package com.agro.trace.lots.service;

import com.agro.trace.common.domain.ActionType;
import com.agro.trace.common.domain.LotStatus;
import com.agro.trace.common.dto.PagedResponse;
import com.agro.trace.common.exception.BusinessException;
import com.agro.trace.common.exception.EntityNotFoundException;
import com.agro.trace.common.exception.InvalidStateTransitionException;
import com.agro.trace.lots.domain.Lot;
import com.agro.trace.lots.dto.LotCreateRequest;
import com.agro.trace.lots.dto.LotResponse;
import com.agro.trace.lots.repository.LotRepository;
import com.agro.trace.products.domain.Product;
import com.agro.trace.products.repository.ProductRepository;
import com.agro.trace.qr.service.QrService;
import com.agro.trace.traceability.service.TraceEventService;
import com.agro.trace.users.domain.User;
import com.agro.trace.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LotService {

    private final LotRepository lotRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final QrService qrService;
    private final TraceEventService traceEventService;
    private final com.agro.trace.routing.service.NearestAgentService nearestAgentService;
    private final com.agro.trace.routing.service.MockRoutingService routingService;

    private static final Set<LotStatus> DELETABLE_STATUSES = Set.of(LotStatus.CREATED);
    private static final Set<LotStatus> ACCEPTABLE_STATUSES = Set.of(LotStatus.CREATED);

    @Transactional
    public LotResponse createLot(LotCreateRequest request, String farmerUuid) {
        User farmer = userRepository.findByUuid(farmerUuid)
                .orElseThrow(() -> new EntityNotFoundException("Farmer", farmerUuid));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product", String.valueOf(request.productId())));

        // Generate lot ID
        String lotId = "LOT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Lot lot = new Lot();
        lot.setLotId(lotId);
        lot.setFarmerUuid(farmerUuid);
        lot.setProductId(request.productId());
        lot.setVarietyId(request.varietyId());
        lot.setQuantity(request.quantity());
        lot.setUnit(product.getDefaultUnit());
        lot.setStatus(LotStatus.CREATED);
        lot.setOriginLatitude(request.latitude());
        lot.setOriginLongitude(request.longitude());
        lot.setOriginAddress(request.originAddress());
        lot.setCurrentCustodianUuid(farmerUuid);
        lot.setCurrentCustodianRole("FARMER");
        lot.setEstimatedValue(request.estimatedValue());

        lot = lotRepository.save(lot);

        if (request.latitude() != null && request.longitude() != null) {
            var nearestCenters = nearestAgentService.findNearestCenters(
                    request.latitude().doubleValue(), request.longitude().doubleValue(), 5);
            var activeStatuses = List.of(LotStatus.CREATED, LotStatus.ACCEPTED, LotStatus.AT_SUPPLIER, LotStatus.AT_MANUFACTURER);
            long maxLotsPerCenter = 20;

            for (var center : nearestCenters) {
                Long centerId = Long.valueOf(center.getCenterId().hashCode());
                long currentLoad = lotRepository.countByNodalCenterIdAndStatusIn(centerId, activeStatuses);
                if (currentLoad < maxLotsPerCenter) {
                    lot.setNodalCenterId(centerId);
                    log.info("Assigned center: {} ({}) [load: {}/{}] to lot {}",
                            center.getName(), center.getCity(), currentLoad, maxLotsPerCenter, lotId);
                    lot.setOriginAddress(center.getCity() + ", " + center.getState());
                    break;
                }
                log.info("Center {} at capacity ({}/{}), trying next", center.getCity(), currentLoad, maxLotsPerCenter);
            }
        }


        // Generate QR for the lot
        String qrId = qrService.generateQr("LOT", lotId, "CREATED");
        lot.setQrId(qrId);
        lot = lotRepository.save(lot);

        // Record trace event
        traceEventService.recordEvent(
                ActionType.LOT_CREATED,
                "LOT", lotId,
                farmerUuid, "FARMER",
                null, LotStatus.CREATED.name(),
                request.latitude(), request.longitude(),
                qrId, null
        );

        log.info("Lot created: {} by farmer {}", lotId, farmerUuid);
        return toResponse(lot);
    }

    @Transactional
    public LotResponse acceptLot(String lotId, String agentUuid, BigDecimal latitude, BigDecimal longitude, String qrId) {
        Lot lot = lotRepository.findByLotId(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot", lotId));

        if (!ACCEPTABLE_STATUSES.contains(lot.getStatus())) {
            throw new InvalidStateTransitionException("Lot cannot be accepted in current state: " + lot.getStatus());
        }

        // Validate QR
        qrService.consumeQr(qrId != null ? qrId : lot.getQrId(), agentUuid, latitude, longitude);

        lot.setStatus(LotStatus.ACCEPTED);
        lot.setAcceptedAt(Instant.now());
        lot.setAcceptedBy(agentUuid);
        lot.setCurrentCustodianUuid(agentUuid);
        lot.setCurrentCustodianRole("COLLECTING_AGENT");
        lot = lotRepository.save(lot);

        // Generate new QR for next stage
        String newQrId = qrService.generateQr("LOT", lotId, "ACCEPTED");
        lot.setQrId(newQrId);
        lot = lotRepository.save(lot);

        traceEventService.recordEvent(
                ActionType.LOT_ACCEPTED,
                "LOT", lotId,
                agentUuid, "COLLECTING_AGENT",
                LotStatus.CREATED.name(), LotStatus.ACCEPTED.name(),
                latitude, longitude,
                newQrId, null
        );

        log.info("Lot accepted: {} by agent {}", lotId, agentUuid);
        return toResponse(lot);
    }

    @Transactional
    public void deleteLot(String lotId, String farmerUuid) {
        Lot lot = lotRepository.findByLotId(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot", lotId));

        if (!lot.getFarmerUuid().equals(farmerUuid)) {
            throw new BusinessException("FORBIDDEN_ACCESS", "You can only delete your own lots", 403);
        }

        if (!DELETABLE_STATUSES.contains(lot.getStatus())) {
            throw new InvalidStateTransitionException("Cannot delete lot in status: " + lot.getStatus());
        }

        lot.setActive(false);
        lotRepository.save(lot);

        traceEventService.recordEvent(
                ActionType.LOT_DELETED, "LOT", lotId,
                farmerUuid, "FARMER",
                lot.getStatus().name(), "DELETED",
                null, null, null, null
        );

        log.info("Lot deleted: {} by farmer {}", lotId, farmerUuid);
    }

    public LotResponse getLot(String lotId) {
        Lot lot = lotRepository.findByLotId(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot", lotId));
        return toResponse(lot);
    }

    public PagedResponse<LotResponse> getFarmerLots(String farmerUuid, Pageable pageable) {
        Page<Lot> lots = lotRepository.findByFarmerUuidOrderByCreatedAtDesc(farmerUuid, pageable);
        return PagedResponse.from(lots.map(this::toResponse));
    }

    public PagedResponse<LotResponse> getAgentLots(String agentUuid, Pageable pageable) {
        Page<Lot> lots = lotRepository.findByCurrentCustodianUuidOrderByCreatedAtDesc(agentUuid, pageable);
        return PagedResponse.from(lots.map(this::toResponse));
    }

    public PagedResponse<LotResponse> getAvailableLots(Pageable pageable) {
        Page<Lot> lots = lotRepository.findByStatusAndActiveTrue(LotStatus.CREATED, pageable);
        return PagedResponse.from(lots.map(this::toResponse));
    }

    public PagedResponse<LotResponse> getSupplierLots(String supplierUuid, Pageable pageable) {
        Page<Lot> lots = lotRepository.findByCurrentCustodianUuidAndStatusInOrderByCreatedAtDesc(
                supplierUuid, List.of(LotStatus.AT_SUPPLIER), pageable);
        return PagedResponse.from(lots.map(this::toResponse));
    }

    @Transactional
    public LotResponse deliverToSupplier(String lotId, String agentUuid, BigDecimal latitude, BigDecimal longitude) {
        Lot lot = lotRepository.findByLotId(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot", lotId));

        if (lot.getStatus() != LotStatus.ACCEPTED) {
            throw new InvalidStateTransitionException("Lot cannot be delivered in current state: " + lot.getStatus());
        }

        lot.setStatus(LotStatus.AT_SUPPLIER);
        lot.setCurrentCustodianUuid(null);
        lot.setCurrentCustodianRole(null);
        lot = lotRepository.save(lot);

        String newQrId = qrService.generateQr("LOT", lotId, "AT_SUPPLIER");
        lot.setQrId(newQrId);
        lot = lotRepository.save(lot);

        traceEventService.recordEvent(
                ActionType.CUSTODY_TRANSFERRED,
                "LOT", lotId,
                agentUuid, "COLLECTING_AGENT",
                LotStatus.ACCEPTED.name(), LotStatus.AT_SUPPLIER.name(),
                latitude, longitude, newQrId, null
        );

        log.info("Lot delivered to supplier: {} by agent {}", lotId, agentUuid);
        return toResponse(lot);
    }

    public List<LotResponse> getLotsByStatus(LotStatus status) {
        return lotRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public LotResponse transferLot(String lotId, String supplierUuid) {
        Lot lot = lotRepository.findByLotId(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot", lotId));

        if (!Set.of(LotStatus.AT_SUPPLIER, LotStatus.ACCEPTED).contains(lot.getStatus())) {
            throw new InvalidStateTransitionException("Lot cannot be transferred in current state: " + lot.getStatus());
        }

        lot.setStatus(LotStatus.AT_MANUFACTURER);
        lot.setCurrentCustodianUuid(null);
        lot.setCurrentCustodianRole(null);
        lot = lotRepository.save(lot);

        String newQrId = qrService.generateQr("LOT", lotId, "AT_MANUFACTURER");
        lot.setQrId(newQrId);
        lot = lotRepository.save(lot);

        traceEventService.recordEvent(
                ActionType.CUSTODY_TRANSFERRED,
                "LOT", lotId,
                supplierUuid, "SUPPLIER",
                LotStatus.AT_SUPPLIER.name(), LotStatus.AT_MANUFACTURER.name(),
                null, null, newQrId, null
        );

        log.info("Lot transferred from supplier to manufacturer: {} by {}", lotId, supplierUuid);
        return toResponse(lot);
    }

    private LotResponse toResponse(Lot lot) {
        return new LotResponse(
                lot.getLotId(),
                lot.getFarmerUuid(),
                lot.getProductId(),
                lot.getVarietyId(),
                lot.getQuantity(),
                lot.getUnit(),
                lot.getStatus(),
                lot.getOriginLatitude(),
                lot.getOriginLongitude(),
                lot.getOriginAddress(),
                lot.getEstimatedValue(),
                lot.getCurrentCustodianUuid(),
                lot.getCurrentCustodianRole(),
                lot.getQrId(),
                lot.getAcceptedAt(),
                lot.getCreatedAt(),
                lot.getUpdatedAt(),
                lot.isRecalled(),
                lot.getNotes()
        );
    }
}