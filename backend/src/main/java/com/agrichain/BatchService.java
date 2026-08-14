package com.agrichain.batch.service;

import com.agrichain.batch.dto.*;
import com.agrichain.batch.entity.Batch;
import com.agrichain.batch.entity.BatchStateMachine;
import com.agrichain.batch.entity.TraceabilityEvent;
import com.agrichain.batch.repository.BatchRepository;
import com.agrichain.batch.repository.TraceabilityEventRepository;
import com.agrichain.common.dto.PageResponse;
import com.agrichain.common.enums.BatchStatus;
import com.agrichain.common.exception.AuthorizationException;
import com.agrichain.common.exception.BusinessRuleException;
import com.agrichain.common.exception.ResourceNotFoundException;
import com.agrichain.farm.entity.Farm;
import com.agrichain.farm.repository.FarmRepository;
import com.agrichain.farmer.entity.Farmer;
import com.agrichain.farmer.repository.FarmerRepository;
import com.agrichain.identity.entity.User;
import com.agrichain.identity.repository.UserRepository;
import com.agrichain.product.entity.Product;
import com.agrichain.product.repository.ProductRepository;
import com.agrichain.security.AuthenticatedUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Batch management service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BatchService {

    private final BatchRepository batchRepository;
    private final TraceabilityEventRepository traceabilityEventRepository;
    private final FarmRepository farmRepository;
    private final FarmerRepository farmerRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Create a new batch
     */
    @Transactional
    public BatchDto createBatch(CreateBatchDto request, AuthenticatedUser auth) {
        // Check for idempotency
        if (request.getClientOperationId() != null) {
            Batch existing = batchRepository
                    .findByClientOperationIdAndDeletedAtIsNull(request.getClientOperationId())
                    .orElse(null);
            if (existing != null) {
                return mapBatchToDto(existing);
            }
        }

        // Get farm and verify access
        Farm farm = farmRepository.findByIdWithDetails(request.getFarmId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm", request.getFarmId()));

        Farmer farmer = farm.getFarmer();
        if (!farmer.getUser().getId().equals(auth.getUserId()) &&
            !auth.belongsToOrganization(farm.getOrganization() != null ? farm.getOrganization().getId() : null)) {
            throw AuthorizationException.resourceOwnership("farm");
        }

        // Get product
        Product product = productRepository.findByIdAndDeletedAtIsNull(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));

        // Generate batch code
        String batchCode = generateBatchCode();

        // Create batch
        Batch batch = Batch.builder()
                .batchCode(batchCode)
                .product(product)
                .farm(farm)
                .farmer(farmer)
                .organization(farm.getOrganization())
                .currentHolder(farm.getOrganization())
                .quantity(request.getQuantity())
                .remainingQuantity(request.getQuantity())
                .unit(request.getUnit())
                .status(BatchStatus.CREATED)
                .harvestDate(request.getHarvestDate())
                .expectedExpiryDate(request.getExpectedExpiryDate())
                .farmingMethod(request.getFarmingMethod())
                .cultivationStartDate(request.getCultivationStartDate())
                .location(toJson(request.getLocation()))
                .notes(request.getNotes())
                .certifications(toJson(request.getCertifications()))
                .qualityGrade(request.getQualityGrade())
                .pricePerUnit(request.getPricePerUnit())
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .clientOperationId(request.getClientOperationId())
                .metadata(toJson(request.getMetadata()))
                .build();

        batch = batchRepository.save(batch);

        // Create traceability event
        createTraceabilityEvent(batch, "BATCH_CREATED", null, "CREATED", auth, request.getLocation());

        log.info("Created batch: {} for farm: {}", batchCode, farm.getId());

        return mapBatchToDto(batch);
    }

    /**
     * Get batch by ID
     */
    @Transactional(readOnly = true)
    public BatchDto getBatchById(UUID batchId, AuthenticatedUser auth) {
        Batch batch = batchRepository.findByIdWithDetails(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch", batchId));

        // Authorization check - simplified for now
        // In production, add proper access control based on role and organization

        return mapBatchToDto(batch);
    }

    /**
     * List batches with filters
     */
    @Transactional(readOnly = true)
    public PageResponse<BatchListDto> listBatches(BatchFilterDto filter, AuthenticatedUser auth) {
        PageRequest pageRequest = PageRequest.of(
                filter.getPage() - 1,
                filter.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Batch> page = batchRepository.findWithFilters(
                filter.getStatus(),
                filter.getFarmerId(),
                filter.getFarmId(),
                filter.getProductId(),
                filter.getDateFrom(),
                filter.getDateTo(),
                pageRequest
        );

        return PageResponse.from(page, this::mapBatchToListDto);
    }

    /**
     * Update batch
     */
    @Transactional
    public BatchDto updateBatch(UUID batchId, UpdateBatchDto request, AuthenticatedUser auth) {
        Batch batch = batchRepository.findByIdWithDetails(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch", batchId));

        // Authorization
        if (!batch.getFarmer().getUser().getId().equals(auth.getUserId())) {
            throw AuthorizationException.resourceOwnership("batch");
        }

        // Optimistic locking
        if (!batch.getVersion().equals(request.getVersion())) {
            throw BusinessRuleException.concurrencyConflict("Batch");
        }

        // Update fields
        if (request.getQuantity() != null) {
            batch.setQuantity(request.getQuantity());
            batch.setRemainingQuantity(request.getQuantity());
        }
        if (request.getExpectedExpiryDate() != null) {
            batch.setExpectedExpiryDate(request.getExpectedExpiryDate());
        }
        if (request.getNotes() != null) {
            batch.setNotes(request.getNotes());
        }
        if (request.getQualityGrade() != null) {
            batch.setQualityGrade(request.getQualityGrade());
        }
        if (request.getPricePerUnit() != null) {
            batch.setPricePerUnit(request.getPricePerUnit());
        }
        if (request.getMetadata() != null) {
            batch.setMetadata(toJson(request.getMetadata()));
        }

        batch = batchRepository.save(batch);

        // Create traceability event
        createTraceabilityEvent(batch, "BATCH_UPDATED", null, null, auth, null);

        return mapBatchToDto(batch);
    }

    /**
     * Transition batch status
     */
    @Transactional
    public BatchDto transitionStatus(UUID batchId, BatchStatusTransitionDto request, AuthenticatedUser auth) {
        Batch batch = batchRepository.findByIdWithDetails(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch", batchId));

        BatchStatus currentStatus = batch.getStatus();
        BatchStatus targetStatus = request.getTargetStatus();

        // Validate transition
        if (!BatchStateMachine.canTransition(currentStatus, targetStatus)) {
            throw BusinessRuleException.invalidStateTransition("Batch", currentStatus.name(), targetStatus.name());
        }

        // Perform transition
        batch.transitionTo(targetStatus);
        batch = batchRepository.save(batch);

        // Create traceability event
        createTraceabilityEvent(batch, "STATUS_CHANGED", currentStatus.name(), targetStatus.name(), auth, request.getLocation());

        log.info("Batch {} transitioned from {} to {}", batchId, currentStatus, targetStatus);

        return mapBatchToDto(batch);
    }

    /**
     * Get batch timeline
     */
    @Transactional(readOnly = true)
    public List<TraceabilityEventDto> getBatchTimeline(UUID batchId, AuthenticatedUser auth) {
        // Verify batch exists
        if (!batchRepository.existsById(batchId)) {
            throw new ResourceNotFoundException("Batch", batchId);
        }

        List<TraceabilityEvent> events = traceabilityEventRepository.findByBatchIdOrderByTimestamp(batchId);

        return events.stream()
                .map(this::mapEventToDto)
                .collect(Collectors.toList());
    }

    // ============================================================
    // Private helpers
    // ============================================================

    private void createTraceabilityEvent(Batch batch, String eventType, String previousState, 
                                         String newState, AuthenticatedUser auth, Object location) {
        User actor = userRepository.findById(auth.getUserId()).orElse(null);
        
        TraceabilityEvent event = TraceabilityEvent.builder()
                .batch(batch)
                .eventType(eventType)
                .actor(actor)
                .previousState(previousState)
                .newState(newState)
                .location(toJson(location))
                .build();
        
        traceabilityEventRepository.save(event);
    }

    private String generateBatchCode() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String random = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        return "AGRI-" + date + "-" + random;
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    private BatchDto mapBatchToDto(Batch batch) {
        return BatchDto.builder()
                .id(batch.getId())
                .batchCode(batch.getBatchCode())
                .product(ProductInfoDto.builder()
                        .id(batch.getProduct().getId())
                        .name(batch.getProduct().getName())
                        .category(batch.getProduct().getCategory())
                        .unit(batch.getProduct().getUnit())
                        .build())
                .farm(FarmInfoDto.builder()
                        .id(batch.getFarm().getId())
                        .name(batch.getFarm().getName())
                        .build())
                .farmer(FarmerInfoDto.builder()
                        .id(batch.getFarmer().getId())
                        .name(batch.getFarmer().getUser().getFullName())
                        .build())
                .quantity(batch.getQuantity())
                .remainingQuantity(batch.getRemainingQuantity())
                .unit(batch.getUnit())
                .status(batch.getStatus())
                .harvestDate(batch.getHarvestDate())
                .expectedExpiryDate(batch.getExpectedExpiryDate())
                .farmingMethod(batch.getFarmingMethod())
                .qualityGrade(batch.getQualityGrade())
                .pricePerUnit(batch.getPricePerUnit())
                .currency(batch.getCurrency())
                .notes(batch.getNotes())
                .riskScore(batch.getRiskScore())
                .qrCodeUrl(batch.getQrCodeUrl())
                .allowedTransitions(BatchStateMachine.getAllowedTransitions(batch.getStatus()))
                .version(batch.getVersion())
                .createdAt(batch.getCreatedAt())
                .updatedAt(batch.getUpdatedAt())
                .build();
    }

    private BatchListDto mapBatchToListDto(Batch batch) {
        return BatchListDto.builder()
                .id(batch.getId())
                .batchCode(batch.getBatchCode())
                .productName(batch.getProduct().getName())
                .farmName(batch.getFarm().getName())
                .quantity(batch.getQuantity())
                .unit(batch.getUnit())
                .status(batch.getStatus())
                .harvestDate(batch.getHarvestDate())
                .qualityGrade(batch.getQualityGrade())
                .version(batch.getVersion())
                .createdAt(batch.getCreatedAt())
                .build();
    }

    private TraceabilityEventDto mapEventToDto(TraceabilityEvent event) {
        return TraceabilityEventDto.builder()
                .id(event.getId())
                .eventType(event.getEventType())
                .previousState(event.getPreviousState())
                .newState(event.getNewState())
                .actorName(event.getActor() != null ? event.getActor().getFullName() : null)
                .actorOrganization(event.getActorOrganization() != null ? event.getActorOrganization().getName() : null)
                .timestamp(event.getServerTimestamp())
                .build();
    }
}
