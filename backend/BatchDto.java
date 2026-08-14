package com.agrichain.batch.dto;

import com.agrichain.common.enums.BatchStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class BatchDto {
    private UUID id;
    private String batchCode;
    private ProductInfoDto product;
    private FarmInfoDto farm;
    private FarmerInfoDto farmer;
    private BigDecimal quantity;
    private BigDecimal remainingQuantity;
    private String unit;
    private BatchStatus status;
    private Instant harvestDate;
    private Instant expectedExpiryDate;
    private String farmingMethod;
    private String qualityGrade;
    private BigDecimal pricePerUnit;
    private String currency;
    private String notes;
    private BigDecimal riskScore;
    private String qrCodeUrl;
    private Set<BatchStatus> allowedTransitions;
    private Integer version;
    private Instant createdAt;
    private Instant updatedAt;
}

@Data
@Builder
class ProductInfoDto {
    private UUID id;
    private String name;
    private String category;
    private String unit;
}

@Data
@Builder
class FarmInfoDto {
    private UUID id;
    private String name;
}

@Data
@Builder
class FarmerInfoDto {
    private UUID id;
    private String name;
}
