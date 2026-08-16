package com.agro.trace.bundles.service;

import com.agro.trace.bundles.domain.Bundle;
import com.agro.trace.bundles.dto.BundleResponse;
import com.agro.trace.bundles.repository.BundleRepository;
import com.agro.trace.common.domain.ActionType;
import com.agro.trace.common.domain.LotStatus;
import com.agro.trace.common.exception.BusinessException;
import com.agro.trace.common.exception.EntityNotFoundException;
import com.agro.trace.common.exception.InvalidStateTransitionException;
import com.agro.trace.qr.service.QrService;
import com.agro.trace.traceability.service.TraceEventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BundleService {

    private final BundleRepository bundleRepository;
    private final QrService qrService;
    private final TraceEventService traceEventService;

    @Transactional
    public BundleResponse receiveBundle(String bundleId, String distributorUuid, BigDecimal latitude, BigDecimal longitude, String qrId) {
        Bundle bundle = getBundleEntity(bundleId);

        if (bundle.getStatus() != LotStatus.BUNDLED) {
            throw new InvalidStateTransitionException("Bundle must be in BUNDLED state to receive");
        }

        qrService.consumeQr(qrId != null ? qrId : bundle.getQrId(), distributorUuid, latitude, longitude);

        bundle.setStatus(LotStatus.AT_DISTRIBUTOR);
        bundle.setCurrentCustodianUuid(distributorUuid);
        bundle.setCurrentCustodianRole("DISTRIBUTOR_EMPLOYEE");

        String newQrId = qrService.generateQr("BUNDLE", bundleId, "AT_DISTRIBUTOR");
        bundle.setQrId(newQrId);
        bundle = bundleRepository.save(bundle);

        traceEventService.recordEvent(
                ActionType.DISTRIBUTOR_RECEIVED, "BUNDLE", bundleId,
                distributorUuid, "DISTRIBUTOR_EMPLOYEE",
                LotStatus.BUNDLED.name(), LotStatus.AT_DISTRIBUTOR.name(),
                latitude, longitude, newQrId, null
        );

        return toResponse(bundle);
    }

    @Transactional
    public BundleResponse verifyDistributor(String bundleId, String distributorUuid) {
        Bundle bundle = getBundleEntity(bundleId);

        if (bundle.getStatus() != LotStatus.AT_DISTRIBUTOR) {
            throw new InvalidStateTransitionException("Bundle must be at distributor to verify");
        }

        bundle.setDistributorVerified(true);
        bundle = bundleRepository.save(bundle);

        log.info("Bundle {} verified by distributor {}", bundleId, distributorUuid);
        return toResponse(bundle);
    }

    @Transactional
    public BundleResponse dispatchToRetailer(String bundleId, String retailerUuid, String distributorUuid) {
        Bundle bundle = getBundleEntity(bundleId);

        if (!bundle.isDistributorVerified()) {
            throw new InvalidStateTransitionException("Bundle must be distributor-verified before dispatch");
        }

        bundle.setStatus(LotStatus.IN_TRANSIT);
        bundle.setCurrentCustodianUuid(distributorUuid);
        bundle = bundleRepository.save(bundle);

        // Generate new QR for retailer stage
        String newQrId = qrService.generateQr("BUNDLE", bundleId, "AT_RETAILER");
        bundle.setQrId(newQrId);
        bundle = bundleRepository.save(bundle);

        traceEventService.recordEvent(
                ActionType.CUSTODY_TRANSFERRED, "BUNDLE", bundleId,
                distributorUuid, "DISTRIBUTOR_EMPLOYEE",
                LotStatus.AT_DISTRIBUTOR.name(), LotStatus.IN_TRANSIT.name(),
                null, null, newQrId, null
        );

        log.info("Bundle {} dispatched to retailer {} by {}", bundleId, retailerUuid, distributorUuid);
        return toResponse(bundle);
    }

    @Transactional
    public BundleResponse retailerReceive(String bundleId, String retailerUuid, BigDecimal latitude, BigDecimal longitude, String qrId) {
        Bundle bundle=getBundleEntity(bundleId);
        // BLOCK if bundle is quarantined or recalled
        if (bundle.isQuarantined()) {
            throw new com.agro.trace.common.exception.InvalidStateTransitionException(
                    "Bundle " + bundleId + " is QUARANTINED. Cannot proceed.");
        }
        if (bundle.isRecalled()) {
            throw new com.agro.trace.common.exception.InvalidStateTransitionException(
                    "Bundle " + bundleId + " is RECALLED. Cannot proceed.");
        }

        // BLOCK if bundle is quarantined or recalled
        if (bundle.isQuarantined()) {
            throw new com.agro.trace.common.exception.InvalidStateTransitionException(
                    "Bundle " + bundleId + " is QUARANTINED. Cannot proceed.");
        }
        if (bundle.isRecalled()) {
            throw new com.agro.trace.common.exception.InvalidStateTransitionException(
                    "Bundle " + bundleId + " is RECALLED. Cannot proceed.");
        }

        if (bundle.getStatus() != LotStatus.IN_TRANSIT && bundle.getStatus() != LotStatus.AT_DISTRIBUTOR) {
            throw new InvalidStateTransitionException("Bundle is not available for retailer receipt");
        }

        if (bundle.isRetailerReceived()) {
            throw new BusinessException("RETAILER_RECEIPT_INVALID", "Bundle already received by retailer");
        }

        qrService.consumeQr(qrId != null ? qrId : bundle.getQrId(), retailerUuid, latitude, longitude);

        bundle.setStatus(LotStatus.AT_RETAILER);
        bundle.setCurrentCustodianUuid(retailerUuid);
        bundle.setCurrentCustodianRole("RETAILER");
        bundle.setRetailerReceived(true);
        bundle.setRetailerReceivedAt(Instant.now());
        bundle.setRetailerUuid(retailerUuid);
        bundle = bundleRepository.save(bundle);

        traceEventService.recordEvent(
                ActionType.RETAILER_RECEIVED, "BUNDLE", bundleId,
                retailerUuid, "RETAILER",
                LotStatus.IN_TRANSIT.name(), LotStatus.AT_RETAILER.name(),
                latitude, longitude, bundle.getQrId(), null
        );

        log.info("Bundle {} received by retailer {}", bundleId, retailerUuid);
        return toResponse(bundle);
    }

    public BundleResponse getBundle(String bundleId) {
        return toResponse(getBundleEntity(bundleId));
    }

    public List<BundleResponse> getAvailableBundles(String custodianUuid) {
        return bundleRepository.findByCurrentCustodianUuid(custodianUuid).stream()
                .map(this::toResponse)
                .toList();
    }

    private Bundle getBundleEntity(String bundleId) {
        return bundleRepository.findByBundleId(bundleId)
                .orElseThrow(() -> new EntityNotFoundException("Bundle", bundleId));
    }

    private BundleResponse toResponse(Bundle bundle) {
        return new BundleResponse(
                bundle.getBundleId(), bundle.getManufacturerLotId(), bundle.getBundleType(),
                bundle.getQuantity(), bundle.getUnit(), bundle.getStatus(),
                bundle.getCurrentCustodianUuid(), bundle.getCurrentCustodianRole(),
                bundle.getQrId(), bundle.isRecalled(), bundle.isQuarantined(),
                bundle.isRetailerReceived(), bundle.isDistributorVerified(),
                bundle.getNotes(), bundle.getCreatedAt()
        );
    }
}