package com.agro.trace.consumer.controller;

import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.consumer.dto.ProductVerificationResponse;
import com.agro.trace.consumer.service.ConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class ConsumerController {

    private final ConsumerService consumerService;

    @GetMapping("/products/{qrToken}")
    public ResponseEntity<ApiResponse<ProductVerificationResponse>> verifyProduct(
            @PathVariable String qrToken) {
        var response = consumerService.verifyProduct(qrToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/products/{qrToken}/trace")
    public ResponseEntity<ApiResponse<?>> getProductTrace(
            @PathVariable String qrToken) {
        var trace = consumerService.getProductTraceSummary(qrToken);
        return ResponseEntity.ok(ApiResponse.success(trace));
    }
}