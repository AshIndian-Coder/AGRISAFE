package com.agro.trace.lots.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record LotCreateRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        Long varietyId,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        BigDecimal quantity,

        String unit,

        BigDecimal latitude,
        BigDecimal longitude,

        String originAddress,

        BigDecimal estimatedValue,

        String notes
) {}