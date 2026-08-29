package com.agro.trace.retail.controller;

import com.agro.trace.bundles.dto.BundleResponse;
import com.agro.trace.bundles.service.BundleService;
import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.complaints.dto.ComplaintRequest;
import com.agro.trace.complaints.dto.ComplaintResponse;
import com.agro.trace.complaints.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/retailers")
@RequiredArgsConstructor
public class RetailerController {

    private final com.agro.trace.testing.service.TestingService testingService;
    private final BundleService bundleService;
    private final ComplaintService complaintService;

    @GetMapping("/bundles")
    public ResponseEntity<ApiResponse<List<BundleResponse>>> getBundles(Authentication auth) {
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
        var response = bundleService.retailerReceive(bundleId, authentication.getName(),
                latitude != null ? java.math.BigDecimal.valueOf(latitude) : null,
                longitude != null ? java.math.BigDecimal.valueOf(longitude) : null,
                qrId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/bundles/{bundleId}")
    public ResponseEntity<ApiResponse<BundleResponse>> getBundle(@PathVariable String bundleId) {
        var response = bundleService.getBundle(bundleId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/complaints")
    public ResponseEntity<ApiResponse<ComplaintResponse>> registerComplaint(
            @Valid @RequestBody ComplaintRequest request,
            Authentication authentication) {
        var response = complaintService.registerComplaint(request, authentication.getName(), "RETAILER");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @GetMapping("/complaints")
    public ResponseEntity<?> getComplaints(Authentication auth) {
        var response = complaintService.getComplainantComplaints(auth.getName(),
                org.springframework.data.domain.PageRequest.of(0, 100));
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