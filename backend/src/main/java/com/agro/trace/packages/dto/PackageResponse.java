package com.agro.trace.packages.dto;

import com.agro.trace.common.domain.LotStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record PackageResponse(
        String packageId,
        String lotId,
        BigDecimal quantity,
        String unit,
        String packageType,
        LotStatus status,
        String currentCustodianUuid,
        String currentCustodianRole,
        String qrId,
        String testingStatus,
        boolean quarantined,
        boolean recalled,
        String notes,
        Instant createdAt
) {}