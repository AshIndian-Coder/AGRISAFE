package com.agro.trace.agents.controller;

import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.common.dto.PagedResponse;
import com.agro.trace.lots.dto.LotResponse;
import com.agro.trace.lots.service.LotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agents")
@RequiredArgsConstructor
public class AgentController {

    private final com.agro.trace.testing.service.TestingService testingService;
    private final LotService lotService;

    @GetMapping("/lots/available")
    public ResponseEntity<ApiResponse<PagedResponse<LotResponse>>> getAvailableLots(
            Authentication authentication,
            Pageable pageable) {
        var response = lotService.getAvailableLots(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/lots/assigned")
    public ResponseEntity<ApiResponse<PagedResponse<LotResponse>>> getAssignedLots(
            Authentication authentication,
            Pageable pageable) {
        var response = lotService.getAgentLots(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/lots/{lotId}")
    public ResponseEntity<ApiResponse<LotResponse>> getLotDetails(@PathVariable String lotId) {
        var response = lotService.getLot(lotId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/lots/{lotId}/accept")
    public ResponseEntity<ApiResponse<LotResponse>> acceptLot(
            @PathVariable String lotId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String qrId,
            Authentication authentication) {
        var response = lotService.acceptLot(lotId, authentication.getName(),
                latitude != null ? java.math.BigDecimal.valueOf(latitude) : null,
                longitude != null ? java.math.BigDecimal.valueOf(longitude) : null,
                qrId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/lots/{lotId}/deliver")
    public ResponseEntity<ApiResponse<LotResponse>> deliverToSupplier(
            @PathVariable String lotId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            Authentication authentication) {
        var response = lotService.deliverToSupplier(lotId, authentication.getName(),
                latitude != null ? java.math.BigDecimal.valueOf(latitude) : null,
                longitude != null ? java.math.BigDecimal.valueOf(longitude) : null);
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