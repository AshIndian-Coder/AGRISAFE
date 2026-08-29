package com.agro.trace.consumer.dto;

import java.time.Instant;

public record ProductVerificationResponse(
        String verificationStatus,
        String productName,
        String manufacturer,
        Instant manufacturedAt,
        String qualityStatus,
        boolean traceabilityComplete,
        boolean retailerReceived,
        boolean recalled,
        String reason,
        int traceEventCount
) {}