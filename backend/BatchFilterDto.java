package com.agrichain.batch.dto;

import com.agrichain.common.enums.BatchStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class BatchFilterDto {

    private BatchStatus status;
    private UUID productId;
    private UUID farmId;
    private UUID farmerId;
    private Instant dateFrom;
    private Instant dateTo;
    private String search;

    @Min(1)
    private int page = 1;

    @Min(1)
    @Max(100)
    private int pageSize = 20;
}
