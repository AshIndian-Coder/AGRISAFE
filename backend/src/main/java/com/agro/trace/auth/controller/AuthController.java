package com.agro.trace.auth.controller;

import com.agro.trace.auth.dto.*;
import com.agro.trace.auth.service.AuthService;
import com.agro.trace.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/farmer/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerFarmer(
            @Valid @RequestBody FarmerRegistrationRequest request) {
        var response = authService.registerFarmer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @PostMapping("/pf/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerPfUser(
            @Valid @RequestBody PfRegistrationRequest request) {
        var response = authService.registerPfUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @PostMapping("/employee/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerEmployee(
            @Valid @RequestBody EmployeeRegistrationRequest request) {
        var response = authService.registerEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @PostMapping("/retailer/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerRetailer(
            @Valid @RequestBody RetailerRegistrationRequest request) {
        var response = authService.registerRetailer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        var response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/unlock")
    public ResponseEntity<ApiResponse<AuthResponse>> unlockApp(
            Authentication authentication,
            @RequestParam String pin) {
        var response = authService.unlockApp(authentication.getName(), pin);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestParam String refreshToken) {
        var response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        authService.logout(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }
}