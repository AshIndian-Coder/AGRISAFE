package com.agro.trace.packages.dto;

import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

public record PackageSplitRequest(
        @NotEmpty(message = "At least one package size is required")
        List<BigDecimal> quantities,

        String packageType,

        String notes
) {}