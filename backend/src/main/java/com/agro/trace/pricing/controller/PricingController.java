package com.agro.trace.pricing.controller;

import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.pricing.service.PricingProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingProvider pricingProvider;

    @GetMapping("/estimate")
    public ResponseEntity<ApiResponse<PricingProvider.PriceEstimate>> getEstimate(
            @RequestParam Long productId,
            @RequestParam(required = false) Long varietyId,
            @RequestParam BigDecimal quantity,
            @RequestParam(required = false) String unit) {
        var estimate = pricingProvider.getEstimatedPrice(productId, varietyId, quantity, unit);
        return estimate
                .map(e -> ResponseEntity.ok(ApiResponse.success(e)))
                .orElse(ResponseEntity.notFound().build());
    }
}