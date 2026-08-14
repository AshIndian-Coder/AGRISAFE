package com.agrichain.batch.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
public class UpdateBatchDto {

    @DecimalMin(value = "0.0001", message = "Quantity must be positive")
    @DecimalMax(value = "1000000", message = "Quantity exceeds maximum")
    private BigDecimal quantity;

    private Instant expectedExpiryDate;

    @Size(max = 2000, message = "Notes too long")
    private String notes;

    @Size(max = 50, message = "Quality grade too long")
    private String qualityGrade;

    @DecimalMin(value = "0", message = "Price must be non-negative")
    private BigDecimal pricePerUnit;

    private Map<String, Object> metadata;

    @NotNull(message = "Version is required for optimistic locking")
    private Integer version;
}
