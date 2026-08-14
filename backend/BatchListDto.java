package com.agrichain.batch.dto;

import com.agrichain.common.enums.BatchStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class BatchListDto {
    private UUID id;
    private String batchCode;
    private String productName;
    private String farmName;
    private BigDecimal quantity;
    private String unit;
    private BatchStatus status;
    private Instant harvestDate;
    private String qualityGrade;
    private Integer version;
    private Instant createdAt;
}
