package com.agro.trace.manufacturing.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record ManufacturerLotCreateRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotEmpty(message = "At least one input lot/package is required")
        List<String> inputLotIds,

        @NotNull(message = "Production quantity is required")
        @Positive(message = "Quantity must be positive")
        BigDecimal productionQuantity,

        String unit,

        String facilityName,

        String notes
) {}