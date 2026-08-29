package com.agro.trace.testing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TestSubmitRequest(
        @NotBlank(message = "Package ID is required")
        String packageId,

        @NotNull(message = "Test profile ID is required")
        Long testProfileId,

        Long testDefinitionId,

        Long standardRequirementId,

        @NotBlank(message = "Measured value is required")
        String measuredValue,

        String unit,

        boolean mandatory,

        String qrId
) {}