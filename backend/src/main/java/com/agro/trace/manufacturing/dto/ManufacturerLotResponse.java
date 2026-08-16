package com.agro.trace.manufacturing.dto;

import com.agro.trace.common.domain.LotStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ManufacturerLotResponse(
        String manufacturerLotId,
        Long productId,
        String manufacturerEmployeeUuid,
        BigDecimal productionQuantity,
        String unit,
        String facilityName,
        LotStatus status,
        String testingStatus,
        String qrId,
        List<String> inputLotIds,
        boolean recalled,
        String notes,
        Instant createdAt,
        List<BundleResponse> bundles
) {
    public record BundleResponse(
            String bundleId,
            String bundleType,
            BigDecimal quantity,
            String unit,
            String qrId,
            LotStatus status,
            Instant createdAt
    ) {}
}