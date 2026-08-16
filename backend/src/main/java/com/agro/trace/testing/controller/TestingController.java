package com.agro.trace.testing.controller;

import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.testing.dto.TestResultResponse;
import com.agro.trace.testing.dto.TestSubmitRequest;
import com.agro.trace.testing.service.TestingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/testing")
@RequiredArgsConstructor
public class TestingController {

    private final TestingService testingService;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<TestResultResponse>> submitTest(
            @Valid @RequestBody TestSubmitRequest request,
            Authentication authentication) {
        var response = testingService.submitTest(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/packages/{packageId}/results")
    public ResponseEntity<ApiResponse<List<TestResultResponse>>> getPackageTests(
            @PathVariable String packageId) {
        var response = testingService.getTestHistory(packageId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/results/{testRecordId}")
    public ResponseEntity<ApiResponse<TestResultResponse>> getTestRecord(
            @PathVariable String testRecordId) {
        var response = testingService.getTestRecord(testRecordId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}