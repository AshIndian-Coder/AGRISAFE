package com.agro.trace.packages.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record SupplierAllotmentTransferRequest(
    @NotBlank
    String allotmentQrId,
    List<String> packageIds
) {}
