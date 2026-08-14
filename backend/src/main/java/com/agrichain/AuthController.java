package com.agrichain.identity.controller;

import com.agrichain.common.dto.ApiResponse;
import com.agrichain.identity.dto.*;
import com.agrichain.identity.service.AuthService;
import com.agrichain.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Authentication controller
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and session management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/otp/request")
    @Operation(summary = "Request OTP", description = "Request OTP for phone authentication")
    public ResponseEntity<ApiResponse<OtpRequestResponse>> requestOtp(
            @Valid @RequestBody OtpRequestDto request) {
        
        OtpRequestResponse response = authService.requestOtp(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/otp/verify")
    @Operation(summary = "Verify OTP", description = "Verify OTP and authenticate user")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody OtpVerifyDto request) {
        
        AuthResponse response = authService.verifyOtpAndLogin(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token", description = "Refresh access token using refresh token")
    public ResponseEntity<ApiResponse<TokensDto>> refreshToken(
            @Valid @RequestBody RefreshTokenDto request) {
        
        TokensDto tokens = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revoke current session")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal AuthenticatedUser user) {
        
        authService.logout(user.getSessionId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/sessions")
    @Operation(summary = "Logout All", description = "Revoke all sessions for current user")
    public ResponseEntity<ApiResponse<Integer>> logoutAll(
            @AuthenticationPrincipal AuthenticatedUser user) {
        
        int count = authService.logoutAll(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/sessions")
    @Operation(summary = "List Sessions", description = "Get active sessions for current user")
    public ResponseEntity<ApiResponse<List<SessionDto>>> getSessions(
            @AuthenticationPrincipal AuthenticatedUser user) {
        
        List<SessionDto> sessions = authService.getActiveSessions(user.getUserId());
        
        // Mark current session
        sessions.forEach(s -> s.setCurrent(s.getId().equals(user.getSessionId())));
        
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }
}
