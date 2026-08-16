package com.agro.trace.recall.service;

import com.agro.trace.bundles.repository.BundleRepository;
import com.agro.trace.common.domain.ActionType;
import com.agro.trace.common.domain.FlagSeverity;
import com.agro.trace.common.domain.LotStatus;
import com.agro.trace.common.exception.EntityNotFoundException;
import com.agro.trace.fraud.service.FlagService;
import com.agro.trace.lots.domain.Lot;
import com.agro.trace.lots.repository.LotRepository;
import com.agro.trace.traceability.service.TraceEventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecallService {

    private final LotRepository lotRepository;
    private final BundleRepository bundleRepository;
    private final FlagService flagService;
    private final TraceEventService traceEventService;

    @Transactional
    public void recallLot(String lotId, String reason, String governmentUuid) {
        Lot lot = lotRepository.findByLotId(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot", lotId));

        lot.setRecalled(true);
        lot.setRecalledAt(Instant.now());
        lot.setRecallReason(reason);
        lot.setStatus(LotStatus.RECALLED);
        lotRepository.save(lot);

        // Mark all derived bundles as recalled
        bundleRepository.findByManufacturerLotId(lotId).forEach(b -> {
            b.setRecalled(true);
            bundleRepository.save(b);
        });

        // Create critical flag
        flagService.createFlag("PRODUCT_RECALL", FlagSeverity.CRITICAL,
                "LOT", lotId, governmentUuid,
                "Lot recalled: " + reason, null);

        traceEventService.recordEvent(
                ActionType.RECALL_ISSUED, "LOT", lotId,
                governmentUuid, "GOVERNMENT_EMPLOYEE",
                lot.getStatus().name(), LotStatus.RECALLED.name(),
                null, null, null, null
        );

        log.warn("LOT RECALLED: {} by {} - Reason: {}", lotId, governmentUuid, reason);
    }

    @Transactional
    public void liftRecall(String lotId, String governmentUuid) {
        Lot lot = lotRepository.findByLotId(lotId)
                .orElseThrow(() -> new EntityNotFoundException("Lot", lotId));

        if (!lot.isRecalled()) {
            throw new IllegalStateException("Lot is not recalled: " + lotId);
        }

        lot.setRecalled(false);
        lot.setRecalledAt(null);
        lot.setRecallReason(null);
        lot.setStatus(LotStatus.QUARANTINED);
        lotRepository.save(lot);

        traceEventService.recordEvent(
                ActionType.RECALL_LIFTED, "LOT", lotId,
                governmentUuid, "GOVERNMENT_EMPLOYEE",
                LotStatus.RECALLED.name(), LotStatus.QUARANTINED.name(),
                null, null, null, null
        );

        log.info("RECALL LIFTED for lot {} by {}", lotId, governmentUuid);
    }
}