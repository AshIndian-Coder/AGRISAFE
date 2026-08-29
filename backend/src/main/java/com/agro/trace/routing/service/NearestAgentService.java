package com.agro.trace.routing.service;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * Finds the nearest collection agent / nodal center to a farmer's GPS location.
 * Uses pre-seeded nodal center locations.
 * In production, queries real agent locations from a geospatial database.
 */
@Service
@Slf4j
public class NearestAgentService {

    private final List<NodalCenterLocation> centers = new ArrayList<>();

    @Data
    public static class NodalCenterLocation {
        private final String centerId;
        private final String name;
        private final String city;
        private final String state;
        private final double latitude;
        private final double longitude;
        private final double maxRadiusKm;
    }

    @PostConstruct
    public void init() {
        // Seeded nodal center locations across India (fictional but realistic)
        centers.add(new NodalCenterLocation("NC-MH-001", "Mumbai Nodal Center - Andheri", "Mumbai", "Maharashtra", 19.1136, 72.8697, 50));
        centers.add(new NodalCenterLocation("NC-MH-002", "Mumbai Nodal Center - Kurla", "Mumbai", "Maharashtra", 19.0655, 72.8777, 40));
        centers.add(new NodalCenterLocation("NC-MH-003", "Pune Nodal Center", "Pune", "Maharashtra", 18.5204, 73.8567, 60));
        centers.add(new NodalCenterLocation("NC-MH-004", "Nagpur Nodal Center", "Nagpur", "Maharashtra", 21.1458, 79.0882, 70));
        centers.add(new NodalCenterLocation("NC-MH-005", "Nashik Nodal Center", "Nashik", "Maharashtra", 19.9615, 73.8088, 50));
        centers.add(new NodalCenterLocation("NC-GJ-001", "Surat Nodal Center", "Surat", "Gujarat", 21.1702, 72.8311, 45));
        centers.add(new NodalCenterLocation("NC-GJ-002", "Ahmedabad Nodal Center", "Ahmedabad", "Gujarat", 23.0225, 72.5714, 55));
        centers.add(new NodalCenterLocation("NC-MP-001", "Indore Nodal Center", "Indore", "Madhya Pradesh", 22.7196, 75.8577, 60));
        centers.add(new NodalCenterLocation("NC-MP-002", "Bhopal Nodal Center", "Bhopal", "Madhya Pradesh", 23.2599, 77.4126, 50));
        centers.add(new NodalCenterLocation("NC-UP-001", "Lucknow Nodal Center", "Lucknow", "Uttar Pradesh", 26.8467, 80.9462, 55));
        centers.add(new NodalCenterLocation("NC-UP-002", "Kanpur Nodal Center", "Kanpur", "Uttar Pradesh", 26.4499, 80.3319, 45));
        centers.add(new NodalCenterLocation("NC-KA-001", "Bangalore Nodal Center", "Bangalore", "Karnataka", 12.9716, 77.5946, 60));
        centers.add(new NodalCenterLocation("NC-TN-001", "Chennai Nodal Center", "Chennai", "Tamil Nadu", 13.0827, 80.2707, 50));
        centers.add(new NodalCenterLocation("NC-TS-001", "Hyderabad Nodal Center", "Hyderabad", "Telangana", 17.3850, 78.4867, 55));
        centers.add(new NodalCenterLocation("NC-PB-001", "Ludhiana Nodal Center", "Ludhiana", "Punjab", 30.9010, 75.8573, 50));
        centers.add(new NodalCenterLocation("NC-RJ-001", "Jaipur Nodal Center", "Jaipur", "Rajasthan", 26.9124, 75.7873, 50));
        centers.add(new NodalCenterLocation("NC-WB-001", "Kolkata Nodal Center", "Kolkata", "West Bengal", 22.5726, 88.3639, 45));
        centers.add(new NodalCenterLocation("NC-DL-001", "Delhi Nodal Center", "Delhi", "Delhi", 28.7041, 77.1025, 60));
        log.info("Seeded {} nodal center locations", centers.size());
    }

    /**
     * Finds the nearest nodal center to given GPS coordinates.
     * Returns the top N closest centers within max radius.
     */
    public List<NodalCenterLocation> findNearestCenters(double latitude, double longitude, int limit) {
        return centers.stream()
            .map(c -> new AbstractMap.SimpleEntry<>(c, haversine(latitude, longitude, c.getLatitude(), c.getLongitude())))
            .filter(e -> e.getValue() <= e.getKey().getMaxRadiusKm())
            .sorted(Comparator.comparingDouble(Map.Entry::getValue))
            .limit(limit)
            .peek(e -> log.debug("Center {} is {:.1f} km away", e.getKey().getCenterId(), e.getValue()))
            .map(Map.Entry::getKey)
            .toList();
    }

    /**
     * Finds the single nearest nodal center.
     */
    public NodalCenterLocation findNearest(double latitude, double longitude) {
        var results = findNearestCenters(latitude, longitude, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Haversine formula for distance between two GPS points.
     */
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
