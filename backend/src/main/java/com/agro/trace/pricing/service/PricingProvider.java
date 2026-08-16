package com.agro.trace.pricing.service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Pricing Provider Interface.
 * Provides estimated market pricing for agricultural products.
 * Mock implementation for prototype; connects to real market APIs in production.
 */
public interface PricingProvider {

    /**
     * Get estimated price for a product/variety.
     */
    Optional<PriceEstimate> getEstimatedPrice(Long productId, Long varietyId, BigDecimal quantity, String unit);

    record PriceEstimate(
            BigDecimal pricePerUnit,
            String unit,
            String currency,
            BigDecimal totalEstimatedValue,
            String source,
            String sourceReference,
            boolean isEstimate,
            String disclaimer
    ) {}
}
