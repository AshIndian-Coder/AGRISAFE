package com.agrichain.batch.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class LocationDto {

    @DecimalMin(value = "-90", message = "Invalid latitude")
    @DecimalMax(value = "90", message = "Invalid latitude")
    private BigDecimal latitude;

    @DecimalMin(value = "-180", message = "Invalid longitude")
    @DecimalMax(value = "180", message = "Invalid longitude")
    private BigDecimal longitude;

    private BigDecimal accuracy;
    
    private String source; // GPS, NETWORK, MANUAL, IP

    private Instant capturedAt;
}
