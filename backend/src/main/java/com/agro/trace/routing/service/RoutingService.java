package com.agro.trace.routing.service;

import java.util.List;

/**
 * Routing Service Interface.
 * Determines optimal supply chain paths for agricultural lots.
 * Mock implementation for prototype; connects to real logistics APIs in production.
 */
public interface RoutingService {

    /**
     * Find the optimal route from source to destination.
     */
    RouteResult findOptimalRoute(String sourceLocation, String destinationLocation, String productType);

    /**
     * Validate that a route is physically feasible.
     */
    boolean validateRoute(List<String> hops, String productType);

    /**
     * Get estimated transit time.
     */
    TransitEstimate estimateTransitTime(String from, String to, String transportMode);

    record RouteResult(
            List<String> recommendedHops,
            int totalEstimatedHours,
            String transportMode,
            double distanceKm,
            String disclaimer
    ) {}

    record TransitEstimate(
            int hours,
            double distanceKm,
            String transportMode,
            String confidence
    ) {}
}