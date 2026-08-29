package com.agro.trace.distribution.controller;

import com.agro.trace.bundles.dto.BundleResponse;
import com.agro.trace.bundles.service.BundleService;
import com.agro.trace.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/distributors")
@RequiredArgsConstructor
public class DistributorController {

    private final com.agro.trace.testing.service.TestingService testingService;
    private final BundleService bundleService;

    @GetMapping("/bundles/available")
    public ResponseEntity<ApiResponse<List<BundleResponse>>> getAvailableBundles(Authentication auth) {
        var response = bundleService.getAvailableBundles(auth.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/bundles/{bundleId}/receive")
    public ResponseEntity<ApiResponse<BundleResponse>> receiveBundle(
            @PathVariable String bundleId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String qrId,
            Authentication authentication) {
        var response = bundleService.receiveBundle(bundleId, authentication.getName(),
                latitude != null ? java.math.BigDecimal.valueOf(latitude) : null,
                longitude != null ? java.math.BigDecimal.valueOf(longitude) : null,
                qrId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/bundles/{bundleId}/verify")
    public ResponseEntity<ApiResponse<BundleResponse>> verifyBundle(
            @PathVariable String bundleId,
            Authentication authentication) {
        var response = bundleService.verifyDistributor(bundleId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/bundles/{bundleId}/dispatch/{retailerUuid}")
    public ResponseEntity<ApiResponse<BundleResponse>> dispatchToRetailer(
            @PathVariable String bundleId,
            @PathVariable String retailerUuid,
            Authentication authentication) {
        var response = bundleService.dispatchToRetailer(bundleId, retailerUuid, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
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