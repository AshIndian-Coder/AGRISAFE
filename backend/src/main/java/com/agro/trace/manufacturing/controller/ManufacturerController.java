package com.agro.trace.manufacturing.controller;

import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.manufacturing.dto.ManufacturerLotCreateRequest;
import com.agro.trace.manufacturing.dto.ManufacturerLotResponse;
import com.agro.trace.manufacturing.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manufacturers")
@RequiredArgsConstructor
public class ManufacturerController {

    private final com.agro.trace.testing.service.TestingService testingService;
    private final ManufacturerService manufacturerService;

    @PostMapping("/lots")
    public ResponseEntity<ApiResponse<ManufacturerLotResponse>> createManufacturerLot(
            @Valid @RequestBody ManufacturerLotCreateRequest request,
            Authentication authentication) {
        var response = manufacturerService.createManufacturerLot(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @GetMapping("/lots/{manufacturerLotId}")
    public ResponseEntity<ApiResponse<ManufacturerLotResponse>> getManufacturerLot(
            @PathVariable String manufacturerLotId) {
        var response = manufacturerService.getManufacturerLot(manufacturerLotId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/lots")
    public ResponseEntity<ApiResponse<List<ManufacturerLotResponse>>> getManufacturerLots(
            Authentication authentication) {
        var response = manufacturerService.getManufacturerLots(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/lots/{manufacturerLotId}/bundles")
    public ResponseEntity<ApiResponse<List<ManufacturerLotResponse.BundleResponse>>> createBundles(
            @PathVariable String manufacturerLotId,
            @RequestParam String bundleType,
            @RequestParam int bundleCount,
            Authentication authentication) {
        var response = manufacturerService.createBundles(manufacturerLotId, bundleType, bundleCount,
                authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
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