package com.agro.trace.bundles.dto;

import com.agro.trace.common.domain.LotStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record BundleResponse(
        String bundleId,
        String manufacturerLotId,
        String bundleType,
        BigDecimal quantity,
        String unit,
        LotStatus status,
        String currentCustodianUuid,
        String currentCustodianRole,
        String qrId,
        boolean recalled,
        boolean quarantined,
        boolean retailerReceived,
        boolean distributorVerified,
        String notes,
        Instant createdAt
) {}