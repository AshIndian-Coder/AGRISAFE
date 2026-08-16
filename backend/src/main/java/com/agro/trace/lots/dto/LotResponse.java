package com.agro.trace.lots.dto;

import com.agro.trace.common.domain.LotStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record LotResponse(
        String lotId,
        String farmerUuid,
        Long productId,
        Long varietyId,
        BigDecimal quantity,
        String unit,
        LotStatus status,
        BigDecimal originLatitude,
        BigDecimal originLongitude,
        String originAddress,
        BigDecimal estimatedValue,
        String currentCustodianUuid,
        String currentCustodianRole,
        String qrId,
        Instant acceptedAt,
        Instant createdAt,
        Instant updatedAt,
        boolean recalled,
        String notes
) {}