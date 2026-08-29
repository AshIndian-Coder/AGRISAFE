package com.agro.trace.suppliers.controller;

import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.lots.dto.LotResponse;
import com.agro.trace.lots.service.LotService;
import com.agro.trace.packages.dto.PackageResponse;
import com.agro.trace.packages.service.PackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final com.agro.trace.testing.service.TestingService testingService;
    private final LotService lotService;
    private final PackageService packageService;
    private final com.agro.trace.routing.service.MockRoutingService routingService;

    @GetMapping("/assignments")
    public ResponseEntity<ApiResponse<?>> getAssignments(Authentication auth, Pageable pageable) {
        var lots = lotService.getSupplierLots(auth.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(lots));
    }

    @PostMapping("/lots/{lotId}/transfer")
    public ResponseEntity<ApiResponse<LotResponse>> transferToManufacturer(
            @PathVariable String lotId,
            Authentication authentication) {
        var response = lotService.transferLot(lotId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/packages/{packageId}")
    public ResponseEntity<ApiResponse<PackageResponse>> getPackage(@PathVariable String packageId) {
        var response = packageService.getPackage(packageId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/lots/{lotId}/packages")
    public ResponseEntity<ApiResponse<List<PackageResponse>>> getLotPackages(@PathVariable String lotId) {
        var response = packageService.getLotPackages(lotId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/packages/{packageId}/receive")
    public ResponseEntity<ApiResponse<PackageResponse>> receivePackage(
            @PathVariable String packageId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String qrId,
            Authentication authentication) {
        var response = packageService.verifyPackage(packageId, authentication.getName(),
                latitude != null ? java.math.BigDecimal.valueOf(latitude) : null,
                longitude != null ? java.math.BigDecimal.valueOf(longitude) : null,
                qrId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/route")
    public ResponseEntity<ApiResponse<?>> getRoute(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "GENERAL") String productType) {
        var route = routingService.findOptimalRoute(from, to, productType);
        return ResponseEntity.ok(ApiResponse.success(route));
    }


    @PostMapping("/test")
    public ResponseEntity<ApiResponse<com.agro.trace.testing.dto.TestResultResponse>> submitTest(
            @Valid @RequestBody com.agro.trace.testing.dto.TestSubmitRequest request,
            Authentication authentication) {
        var response = testingService.submitTest(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/tests/{packageId}")
    public ResponseEntity<ApiResponse<java.util.List<com.agro.trace.testing.dto.TestResultResponse>>> getTestHistory(
            @PathVariable String packageId) {
        var response = testingService.getTestHistory(packageId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}