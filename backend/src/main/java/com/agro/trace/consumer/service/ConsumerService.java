package com.agro.trace.consumer.service;

import com.agro.trace.common.domain.LotStatus;
import com.agro.trace.common.domain.TestResult;
import com.agro.trace.common.exception.EntityNotFoundException;
import com.agro.trace.consumer.dto.ProductVerificationResponse;
import com.agro.trace.lots.domain.Lot;
import com.agro.trace.lots.repository.LotRepository;
import com.agro.trace.products.domain.Product;
import com.agro.trace.products.repository.ProductRepository;
import com.agro.trace.testing.repository.TestRecordRepository;
import com.agro.trace.traceability.domain.TraceEvent;
import com.agro.trace.traceability.repository.TraceEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerService {

    private final LotRepository lotRepository;
    private final ProductRepository productRepository;
    private final TraceEventRepository traceEventRepository;
    private final TestRecordRepository testRecordRepository;

    public ProductVerificationResponse verifyProduct(String qrToken) {
        // Try to find a lot by QR
        var lots = lotRepository.findAll();
        Lot lot = lots.stream()
                .filter(l -> qrToken.equals(l.getQrId()) || qrToken.equals(l.getLotId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Product", qrToken));

        Product product = productRepository.findById(lot.getProductId())
                .orElse(null);

        // Run verification checks
        boolean traceabilityComplete = isTraceabilityComplete(lot);
        boolean retailerReceived = lot.getStatus() == LotStatus.AT_RETAILER
                || lot.getStatus() == LotStatus.READY_FOR_SALE
                || lot.getStatus() == LotStatus.SOLD;
        boolean recalled = lot.isRecalled();
        boolean qualityPassed = hasQualityPassed(lot);
        boolean isBlocked = isProductBlocked(lot);

        String verificationStatus;
        String reason = null;

        if (recalled) {
            verificationStatus = "RECALLED";
            reason = "RECALLED / DO NOT CONSUME";
        } else if (isBlocked) {
            verificationStatus = "NOT_VERIFIED";
            reason = "PRODUCT_BLOCKED";
        } else if (!qualityPassed) {
            verificationStatus = "NOT_VERIFIED";
            reason = "QUALITY_CHECK_FAILED";
        } else if (!traceabilityComplete) {
            verificationStatus = "NOT_VERIFIED";
            reason = "TRACEABILITY_INCOMPLETE";
        } else if (!retailerReceived) {
            verificationStatus = "NOT_VERIFIED";
            reason = "PRODUCT_NOT_YET_AT_RETAILER";
        } else {
            verificationStatus = "VERIFIED";
        }

        List<TraceEvent> events = traceEventRepository
                .findByObjectTypeAndObjectIdOrderByEventTimestampAsc("LOT", lot.getLotId());

        return new ProductVerificationResponse(
                verificationStatus,
                product != null ? product.getProductName() : "Unknown",
                "Government Verified Producer",
                lot.getCreatedAt(),
                qualityPassed ? "PASSED" : "NOT_PASSED",
                traceabilityComplete,
                retailerReceived,
                recalled,
                reason,
                events.size()
        );
    }

    public List<TraceEvent> getProductTraceSummary(String qrToken) {
        var lots = lotRepository.findAll();
        Lot lot = lots.stream()
                .filter(l -> qrToken.equals(l.getQrId()) || qrToken.equals(l.getLotId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Product", qrToken));

        return traceEventRepository.findByObjectTypeAndObjectIdOrderByEventTimestampAsc("LOT", lot.getLotId());
    }

    private boolean isTraceabilityComplete(Lot lot) {
        return lot.getAcceptedAt() != null
                && lot.getStatus() != LotStatus.CREATED;
    }

    private boolean hasQualityPassed(Lot lot) {
        var testRecords = testRecordRepository
                .findByObjectTypeAndObjectIdAndResult("LOT", lot.getLotId(), "FAIL");
        return testRecords.isEmpty();
    }

    private boolean isProductBlocked(Lot lot) {
        return lot.getStatus() == LotStatus.QUARANTINED
                || lot.getStatus() == LotStatus.REJECTED
                || lot.getStatus() == LotStatus.SUSPENDED
                || lot.getStatus() == LotStatus.TEST_FAILED;
    }
}