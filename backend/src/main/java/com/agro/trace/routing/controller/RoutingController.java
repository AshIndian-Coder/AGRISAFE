package com.agro.trace.routing.controller;

import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.routing.service.MockRoutingService;
import com.agro.trace.routing.service.NearestAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/routing")
@RequiredArgsConstructor
public class RoutingController {

    private final MockRoutingService routingService;
    private final NearestAgentService nearestAgentService;

    @GetMapping("/route")
    public ResponseEntity<ApiResponse<?>> getRoute(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "GENERAL") String productType) {
        var route = routingService.findOptimalRoute(from, to, productType);
        return ResponseEntity.ok(ApiResponse.success(route));
    }

    @GetMapping("/nearest-center")
    public ResponseEntity<ApiResponse<?>> getNearestCenter(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        var center = nearestAgentService.findNearest(latitude, longitude);
        if (center == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "NO_CENTER_FOUND",
                    "No nodal center found near the specified location"));
        }
        return ResponseEntity.ok(ApiResponse.success(center));
    }

    @GetMapping("/centers")
    public ResponseEntity<ApiResponse<?>> getAllCenters() {
        var centers = nearestAgentService.findNearestCenters(20, 78, 100);
        return ResponseEntity.ok(ApiResponse.success(centers));
    }
}