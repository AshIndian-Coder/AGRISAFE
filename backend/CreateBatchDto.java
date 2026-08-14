package com.agrichain.batch.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class CreateBatchDto {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Farm ID is required")
    private UUID farmId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0001", message = "Quantity must be positive")
    @DecimalMax(value = "1000000", message = "Quantity exceeds maximum")
    private BigDecimal quantity;

    @NotBlank(message = "Unit is required")
    @Pattern(regexp = "^(KG|QUINTAL|TON|LITRE|PIECE|DOZEN|BUNCH)$", message = "Invalid unit")
    private String unit;

    @NotNull(message = "Harvest date is required")
    private Instant harvestDate;

    private Instant expectedExpiryDate;

    @Pattern(regexp = "^(ORGANIC|CONVENTIONAL|HYDROPONIC|INTEGRATED)$", message = "Invalid farming method")
    private String farmingMethod;

    private Instant cultivationStartDate;

    private LocationDto location;

    @Size(max = 2000, message = "Notes too long")
    private String notes;

    @Size(max = 20, message = "Too many certifications")
    private List<String> certifications;

    @Size(max = 50, message = "Quality grade too long")
    private String qualityGrade;

    @DecimalMin(value = "0", message = "Price must be non-negative")
    private BigDecimal pricePerUnit;

    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency;

    private Map<String, Object> metadata;

    @Size(max = 128, message = "Client operation ID too long")
    private String clientOperationId;
}
