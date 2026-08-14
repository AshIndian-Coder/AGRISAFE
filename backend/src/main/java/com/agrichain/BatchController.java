package com.agrichain.batch.controller;

import com.agrichain.batch.dto.*;
import com.agrichain.batch.service.BatchService;
import com.agrichain.common.dto.ApiResponse;
import com.agrichain.common.dto.PageResponse;
import com.agrichain.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Batch management controller
 */
@RestController
@RequestMapping("/v1/batches")
@RequiredArgsConstructor
@Tag(name = "Batches", description = "Batch lifecycle management")
public class BatchController {

    private final BatchService batchService;

    @PostMapping
    @Operation(summary = "Create Batch", description = "Create a new batch")
    public ResponseEntity<ApiResponse<BatchDto>> createBatch(
            @Valid @RequestBody CreateBatchDto request,
            @AuthenticationPrincipal AuthenticatedUser auth) {
        
        BatchDto batch = batchService.createBatch(request, auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(batch));
    }

    @GetMapping
    @Operation(summary = "List Batches", description = "List batches with filtering")
    public ResponseEntity<ApiResponse<PageResponse<BatchListDto>>> listBatches(
            @Valid BatchFilterDto filter,
            @AuthenticationPrincipal AuthenticatedUser auth) {
        
        PageResponse<BatchListDto> batches = batchService.listBatches(filter, auth);
        return ResponseEntity.ok(ApiResponse.success(batches));
    }

    @GetMapping("/{batchId}")
    @Operation(summary = "Get Batch", description = "Get batch details by ID")
    public ResponseEntity<ApiResponse<BatchDto>> getBatch(
            @PathVariable UUID batchId,
            @AuthenticationPrincipal AuthenticatedUser auth) {
        
        BatchDto batch = batchService.getBatchById(batchId, auth);
        return ResponseEntity.ok(ApiResponse.success(batch));
    }

    @PatchMapping("/{batchId}")
    @Operation(summary = "Update Batch", description = "Update batch details")
    public ResponseEntity<ApiResponse<BatchDto>> updateBatch(
            @PathVariable UUID batchId,
            @Valid @RequestBody UpdateBatchDto request,
            @AuthenticationPrincipal AuthenticatedUser auth) {
        
        BatchDto batch = batchService.updateBatch(batchId, request, auth);
        return ResponseEntity.ok(ApiResponse.success(batch));
    }

    @PostMapping("/{batchId}/status")
    @Operation(summary = "Transition Status", description = "Transition batch to new status")
    public ResponseEntity<ApiResponse<BatchDto>> transitionStatus(
            @PathVariable UUID batchId,
            @Valid @RequestBody BatchStatusTransitionDto request,
            @AuthenticationPrincipal AuthenticatedUser auth) {
        
        BatchDto batch = batchService.transitionStatus(batchId, request, auth);
        return ResponseEntity.ok(ApiResponse.success(batch));
    }

    @GetMapping("/{batchId}/timeline")
    @Operation(summary = "Get Timeline", description = "Get batch traceability timeline")
    public ResponseEntity<ApiResponse<List<TraceabilityEventDto>>> getTimeline(
            @PathVariable UUID batchId,
            @AuthenticationPrincipal AuthenticatedUser auth) {
        
        List<TraceabilityEventDto> timeline = batchService.getBatchTimeline(batchId, auth);
        return ResponseEntity.ok(ApiResponse.success(timeline));
    }
}
