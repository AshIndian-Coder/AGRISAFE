package com.agro.trace.farmers.controller;

import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.common.dto.PagedResponse;
import com.agro.trace.complaints.dto.ComplaintRequest;
import com.agro.trace.complaints.dto.ComplaintResponse;
import com.agro.trace.complaints.service.ComplaintService;
import com.agro.trace.lots.dto.LotCreateRequest;
import com.agro.trace.lots.dto.LotResponse;
import com.agro.trace.lots.service.LotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/farmer")
@RequiredArgsConstructor
public class FarmerController {

    private final LotService lotService;
    private final ComplaintService complaintService;

    @PostMapping("/lots")
    public ResponseEntity<ApiResponse<LotResponse>> createLot(
            @Valid @RequestBody LotCreateRequest request,
            Authentication authentication) {
        var response = lotService.createLot(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @GetMapping("/lots")
    public ResponseEntity<ApiResponse<PagedResponse<LotResponse>>> getMyLots(
            Authentication authentication,
            Pageable pageable) {
        var response = lotService.getFarmerLots(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/lots/{lotId}")
    public ResponseEntity<ApiResponse<Void>> deleteLot(
            @PathVariable String lotId,
            Authentication authentication) {
        lotService.deleteLot(lotId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(null, "Lot deleted successfully"));
    }

    @PostMapping("/complaints")
    public ResponseEntity<ApiResponse<ComplaintResponse>> registerComplaint(
            @Valid @RequestBody ComplaintRequest request,
            Authentication authentication) {
        var response = complaintService.registerComplaint(request, authentication.getName(), "FARMER");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @GetMapping("/complaints")
    public ResponseEntity<ApiResponse<PagedResponse<ComplaintResponse>>> getMyComplaints(
            Authentication authentication,
            Pageable pageable) {
        var response = complaintService.getComplainantComplaints(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}