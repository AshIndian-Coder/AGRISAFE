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

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/otp/send")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendOtp(@Valid @RequestBody OtpSendRequest request) {
        authService.sendOtp(request.email(), "LOGIN");
        return ResponseEntity.ok(ApiResponse.success(Map.of("sent", true), "OTP sent to your email"));
    }

    @PostMapping("/otp/send-signup")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendSignupOtp(@Valid @RequestBody OtpSendRequest request) {
        authService.sendOtp(request.email(), "SIGNUP");
        return ResponseEntity.ok(ApiResponse.success(Map.of("sent", true), "OTP sent to your email"));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        authService.verifyOtp(request.email(), request.otp(), "LOGIN");
        boolean userExists = authService.emailExists(request.email());
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "verified", true,
                "userExists", userExists,
                "email", request.email()
        )));
    }

    @PostMapping("/otp/verify-signup")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifySignupOtp(@Valid @RequestBody OtpVerifyRequest request) {
        authService.verifyOtp(request.email(), request.otp(), "SIGNUP");
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "verified", true,
                "email", request.email()
        )));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@Valid @RequestBody SignupRequest request) {
        var response = authService.signup(request);
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