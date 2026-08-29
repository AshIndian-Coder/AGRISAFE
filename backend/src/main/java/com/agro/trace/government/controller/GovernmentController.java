package com.agro.trace.government.controller;

import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.common.dto.PagedResponse;
import com.agro.trace.complaints.dto.ComplaintResponse;
import com.agro.trace.complaints.service.ComplaintService;
import com.agro.trace.fraud.domain.Flag;
import com.agro.trace.fraud.service.FlagService;
import com.agro.trace.lots.dto.LotResponse;
import com.agro.trace.lots.service.LotService;
import com.agro.trace.traceability.domain.TraceEvent;
import com.agro.trace.traceability.service.TraceEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/government")
@RequiredArgsConstructor
public class GovernmentController {

    private final FlagService flagService;
    private final ComplaintService complaintService;
    private final LotService lotService;
    private final TraceEventService traceEventService;

    // Flags
    @GetMapping("/flags")
    public ResponseEntity<ApiResponse<PagedResponse<Flag>>> getFlags(Pageable pageable) {
        var response = flagService.getAllFlags(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/flags/{id}")
    public ResponseEntity<ApiResponse<Flag>> getFlag(@PathVariable Long id) {
        var response = flagService.getFlag(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/flags/{id}/assign")
    public ResponseEntity<ApiResponse<Flag>> assignInvestigator(
            @PathVariable Long id,
            @RequestParam String investigatorUuid) {
        var response = flagService.assignInvestigator(id, investigatorUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/flags/{id}/resolve")
    public ResponseEntity<ApiResponse<Flag>> resolveFlag(
            @PathVariable Long id,
            @RequestParam String resolution) {
        var response = flagService.resolveFlag(id, resolution);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Complaints
    @GetMapping("/complaints")
    public ResponseEntity<ApiResponse<PagedResponse<ComplaintResponse>>> getComplaints(Pageable pageable) {
        var response = complaintService.getAllComplaints(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/complaints/{complaintId}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaint(@PathVariable String complaintId) {
        var response = complaintService.getComplaint(complaintId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/complaints/{complaintId}/resolve")
    public ResponseEntity<ApiResponse<ComplaintResponse>> resolveComplaint(
            @PathVariable String complaintId,
            @RequestParam String resolution,
            Authentication authentication) {
        var response = complaintService.resolveComplaint(complaintId, resolution, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Full Investigation - Lot History
    @GetMapping("/lots/{lotId}/full-history")
    public ResponseEntity<ApiResponse<?>> getFullLotHistory(@PathVariable String lotId) {
        LotResponse lot = lotService.getLot(lotId);
        List<TraceEvent> trace = traceEventService.getObjectTraceAll("LOT", lotId);
        List<Flag> flags = flagService.getFlagsByEntity("LOT", lotId);

        return ResponseEntity.ok(ApiResponse.success(new FullInvestigationResponse(lot, trace, flags)));
    }

    @GetMapping("/lots/{lotId}/lineage/forward")
    public ResponseEntity<ApiResponse<?>> getForwardLineage(@PathVariable String lotId) {
        // Forward lineage: Lot -> Packages -> Manufacturer Lots -> Bundles -> Retailer
        var lineage = lotService.getLot(lotId);
        return ResponseEntity.ok(ApiResponse.success(lineage));
    }

    @GetMapping("/lots/{lotId}/lineage/reverse")
    public ResponseEntity<ApiResponse<?>> getReverseLineage(@PathVariable String lotId) {
        // Reverse lineage: Farmer -> Collection Agent -> Nodal Center -> ...
        var trace = traceEventService.getObjectTraceAll("LOT", lotId);
        return ResponseEntity.ok(ApiResponse.success(trace));
    }

    record FullInvestigationResponse(
            LotResponse lot,
            List<TraceEvent> traceEvents,
            List<Flag> flags
    ) {}
}