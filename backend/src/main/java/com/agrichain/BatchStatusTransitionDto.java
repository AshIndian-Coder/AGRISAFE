package com.agrichain.batch.dto;

import com.agrichain.common.enums.BatchStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class BatchStatusTransitionDto {

    @NotNull(message = "Target status is required")
    private BatchStatus targetStatus;

    @Size(max = 500, message = "Reason too long")
    private String reason;

    private LocationDto location;

    private Map<String, Object> metadata;
}
