package com.agro.trace.pricing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class MockPricingProvider implements PricingProvider {

    private static final Map<String, BigDecimal> BASE_PRICES = Map.ofEntries(
        Map.entry("MILK-001", BigDecimal.valueOf(56.00)),
        Map.entry("OIL-001", BigDecimal.valueOf(180.00)),
        Map.entry("RICE-001", BigDecimal.valueOf(45.00)),
        Map.entry("WHEAT-001", BigDecimal.valueOf(28.00)),
        Map.entry("COTTON-001", BigDecimal.valueOf(75.00)),
        Map.entry("DAL-001", BigDecimal.valueOf(95.00)),
        Map.entry("TURMERIC-001", BigDecimal.valueOf(120.00)),
        Map.entry("CHILLI-001", BigDecimal.valueOf(160.00)),
        Map.entry("TEA-001", BigDecimal.valueOf(250.00)),
        Map.entry("COFFEE-001", BigDecimal.valueOf(350.00)),
        Map.entry("JOWAR-001", BigDecimal.valueOf(22.00)),
        Map.entry("BAJRA-001", BigDecimal.valueOf(20.00)),
        Map.entry("RUBBER-001", BigDecimal.valueOf(150.00)),
        Map.entry("GROUNDNUT-001", BigDecimal.valueOf(85.00)),
        Map.entry("PEPPER-001", BigDecimal.valueOf(550.00))
    );

    @Override
    public Optional<PriceEstimate> getEstimatedPrice(Long productId, Long varietyId, BigDecimal quantity, String unit) {
        String productCode = "MILK-001"; // Mock lookup
        BigDecimal basePrice = BASE_PRICES.getOrDefault(productCode, BigDecimal.valueOf(50));

        BigDecimal total = basePrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);

        PriceEstimate estimate = new PriceEstimate(
            basePrice, unit != null ? unit : "Kg", "INR", total,
            "Mock Market Price Provider", "MOCK-SRC-" + System.currentTimeMillis(),
            true,
            "This is an estimated price for prototype demonstration. Actual market prices may vary."
        );

        log.info("Price estimate for product {} (qty {}): {}", productCode, quantity, total);
        return Optional.of(estimate);
    }
}
