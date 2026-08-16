package com.agro.trace.routing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
public class MockRoutingService implements RoutingService {

    private static final Map<String, Double> DISTANCES = new HashMap<>();
    static {
        DISTANCES.put("MUMBAI,PUNE", 150.0);    DISTANCES.put("PUNE,MUMBAI", 150.0);
        DISTANCES.put("MUMBAI,NASHIK", 170.0);  DISTANCES.put("NASHIK,MUMBAI", 170.0);
        DISTANCES.put("MUMBAI,NAGPUR", 830.0);  DISTANCES.put("NAGPUR,MUMBAI", 830.0);
        DISTANCES.put("MUMBAI,SURAT", 265.0);   DISTANCES.put("SURAT,MUMBAI", 265.0);
        DISTANCES.put("MUMBAI,AHMEDABAD", 525.0);
        DISTANCES.put("MUMBAI,DELHI", 1420.0);
        DISTANCES.put("PUNE,NASHIK", 210.0);    DISTANCES.put("NASHIK,PUNE", 210.0);
        DISTANCES.put("PUNE,NAGPUR", 690.0);    DISTANCES.put("NAGPUR,PUNE", 690.0);
        DISTANCES.put("PUNE,SATARA", 115.0);
        DISTANCES.put("NAGPUR,INDORE", 450.0);  DISTANCES.put("INDORE,NAGPUR", 450.0);
        DISTANCES.put("SURAT,AHMEDABAD", 260.0);
        DISTANCES.put("MUMBAI,BANGALORE", 980.0);
        DISTANCES.put("BANGALORE,CHENNAI", 350.0);
        DISTANCES.put("BANGALORE,HYDERABAD", 570.0);
        DISTANCES.put("INDORE,MUMBAI", 580.0);
        DISTANCES.put("INDORE,BHOPAL", 230.0);
        DISTANCES.put("KOLKATA,MUMBAI", 2000.0);
    }

    private static final String[] HUBS = {"NASHIK", "PUNE", "SURAT", "INDORE", "NAGPUR", "AHMEDABAD", "HYDERABAD", "BHOPAL"};

    @Override
    public RouteResult findOptimalRoute(String source, String dest, String productType) {
        log.info("Routing {} -> {} for {}", source, dest, productType);
        double direct = getDist(source, dest);
        List<String> hops = new ArrayList<>(List.of(source, dest));

        // For long routes, find optimal intermediate hub
        if (direct > 400) {
            String bestHub = findBestHub(source, dest);
            if (bestHub != null) {
                double viaHub = getDist(source, bestHub) + getDist(bestHub, dest);
                if (viaHub < direct * 1.4) {
                    hops = new ArrayList<>(List.of(source, bestHub, dest));
                }
            }
        }

        int hours = Math.max(1, (int)(direct / 40));
        return new RouteResult(hops, hours, "TRUCK", direct,
            "Prototype route. Actual logistics may vary.");
    }

    @Override
    public boolean validateRoute(List<String> hops, String productType) {
        if (hops == null || hops.size() < 2) return false;
        for (int i = 0; i < hops.size() - 1; i++) {
            if (getDist(hops.get(i), hops.get(i + 1)) <= 0) return false;
        }
        return true;
    }

    @Override
    public TransitEstimate estimateTransitTime(String from, String to, String mode) {
        double dist = getDist(from, to);
        int speed = switch (mode.toUpperCase()) {
            case "TRAIN" -> 60; case "AIR" -> 800; default -> 40;
        };
        return new TransitEstimate(Math.max(1, (int)(dist / speed)), dist, mode, "MEDIUM");
    }

    private double getDist(String a, String b) {
        return DISTANCES.getOrDefault((a + "," + b).toUpperCase(), 500.0);
    }

    private String findBestHub(String source, String dest) {
        String best = null;
        double minTotal = Double.MAX_VALUE;
        for (String hub : HUBS) {
            if (hub.equalsIgnoreCase(source) || hub.equalsIgnoreCase(dest)) continue;
            double total = getDist(source, hub) + getDist(hub, dest);
            if (total > 0 && total < minTotal) { minTotal = total; best = hub; }
        }
        return best;
    }
}
