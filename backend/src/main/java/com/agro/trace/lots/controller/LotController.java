package com.agro.trace.lots.controller;

import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.lots.dto.LotResponse;
import com.agro.trace.lots.service.LotService;
import com.agro.trace.traceability.service.TraceEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/lots")
@RequiredArgsConstructor
public class LotController {

    private final LotService lotService;
    private final TraceEventService traceEventService;

    @GetMapping("/{lotId}")
    public ResponseEntity<ApiResponse<LotResponse>> getLot(@PathVariable String lotId) {
        var response = lotService.getLot(lotId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{lotId}/accept")
    public ResponseEntity<ApiResponse<LotResponse>> acceptLot(
            @PathVariable String lotId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String qrId,
            Authentication authentication) {
        var response = lotService.acceptLot(lotId, authentication.getName(),
                latitude != null ? BigDecimal.valueOf(latitude) : null,
                longitude != null ? BigDecimal.valueOf(longitude) : null,
                qrId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{lotId}/trace")
    public ResponseEntity<ApiResponse<?>> getLotTrace(
            @PathVariable String lotId,
            @RequestParam(required = false) String viewerUuid) {
        var trace = viewerUuid != null
                ? traceEventService.getCustodyWindowTrace("LOT", lotId, viewerUuid)
                : traceEventService.getObjectTraceAll("LOT", lotId);
        return ResponseEntity.ok(ApiResponse.success(trace));
    }
}