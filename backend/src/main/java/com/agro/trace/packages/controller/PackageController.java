package com.agro.trace.packages.controller;

import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.packages.dto.PackageSplitRequest;
import com.agro.trace.packages.dto.PackageResponse;
import com.agro.trace.packages.service.PackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nodal-centers")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @PostMapping("/lots/{lotId}/split")
    public ResponseEntity<ApiResponse<List<PackageResponse>>> splitLot(
            @PathVariable String lotId,
            @Valid @RequestBody PackageSplitRequest request,
            Authentication authentication) {
        var response = packageService.splitLot(lotId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
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

    @PostMapping("/packages/{packageId}/verify")
    public ResponseEntity<ApiResponse<PackageResponse>> verifyPackage(
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
}