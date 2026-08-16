package com.agro.trace.qr.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record QrScanRequest(

        @NotBlank
        String qrId,

        @NotBlank
        String rotatingCode,

        @NotBlank
        String scannedByUuid,

        String scannedByRole,

        BigDecimal latitude,

        BigDecimal longitude
) {
}