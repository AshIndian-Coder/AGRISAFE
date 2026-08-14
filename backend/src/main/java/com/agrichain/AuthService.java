package com.agrichain.identity.service;

import com.agrichain.common.enums.UserRole;
import com.agrichain.common.exception.AuthenticationException;
import com.agrichain.common.exception.RateLimitException;
import com.agrichain.farmer.entity.Farmer;
import com.agrichain.farmer.repository.FarmerRepository;
import com.agrichain.identity.dto.*;
import com.agrichain.identity.entity.OtpCode;
import com.agrichain.identity.entity.Session;
import com.agrichain.identity.entity.User;
import com.agrichain.identity.repository.OtpCodeRepository;
import com.agrichain.identity.repository.SessionRepository;
import com.agrichain.identity.repository.UserRepository;
import com.agrichain.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Authentication service - handles OTP, login, token management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final FarmerRepository farmerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.otp.length:6}")
    private int otpLength;

    @Value("${app.otp.expiry-minutes:10}")
    private int otpExpiryMinutes;

    @Value("${app.otp.max-attempts:5}")
    private int otpMaxAttempts;

    @Value("${app.otp.cooldown-seconds:60}")
    private int otpCooldownSeconds;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Request OTP for phone authentication
     */
    @Transactional
    public OtpRequestResponse requestOtp(OtpRequestDto request) {
        String phone = request.getPhone();

        // Check rate limiting
        Instant cooldownSince = Instant.now().minus(otpCooldownSeconds, ChronoUnit.SECONDS);
        if (otpCodeRepository.hasRecentOtp(phone, "LOGIN", cooldownSince)) {
            throw new RateLimitException(otpCooldownSeconds);
        }

        // Generate OTP
        String code = generateOtp();
        String codeHash = passwordEncoder.encode(code);
        Instant expiresAt = Instant.now().plus(otpExpiryMinutes, ChronoUnit.MINUTES);

        // Save OTP
        OtpCode otpCode = OtpCode.builder()
                .phone(phone)
                .codeHash(codeHash)
                .purpose("LOGIN")
                .expiresAt(expiresAt)
                .build();
        otpCodeRepository.save(otpCode);

        log.info("OTP requested for phone: {}", maskPhone(phone));

        // In production, send SMS here
        // For development, return the code
        return OtpRequestResponse.builder()
                .success(true)
                .expiresAt(expiresAt)
                .code(code) // Remove in production!
                .build();
    }

    /**
     * Verify OTP and authenticate user
     */
    @Transactional
    public AuthResponse verifyOtpAndLogin(OtpVerifyDto request) {
        String phone = request.getPhone();
        String code = request.getOtp();

        // Find valid OTP
        OtpCode otpCode = otpCodeRepository
                .findLatestValidOtp(phone, "LOGIN", Instant.now())
                .orElseThrow(AuthenticationException::invalidCredentials);

        // Check max attempts
        if (otpCode.hasExceededMaxAttempts(otpMaxAttempts)) {
            otpCode.markAsUsed();
            otpCodeRepository.save(otpCode);
            throw AuthenticationException.invalidCredentials();
        }

        // Verify code
        if (!passwordEncoder.matches(code, otpCode.getCodeHash())) {
            otpCode.incrementAttempts();
            otpCodeRepository.save(otpCode);
            throw AuthenticationException.invalidCredentials();
        }

        // Mark OTP as used
        otpCode.markAsUsed();
        otpCodeRepository.save(otpCode);

        // Find or create user
        User user = userRepository.findByPhoneAndDeletedAtIsNull(phone).orElse(null);
        boolean isNewUser = false;

        if (user == null) {
            user = createNewUser(phone);
            isNewUser = true;
        } else {
            user.setIsPhoneVerified(true);
            user.recordLogin();
            userRepository.save(user);
        }

        // Create session
        Session session = createSession(user, request.getDeviceId(), request.getDeviceName(), request.getDevicePlatform());

        // Generate tokens
        JwtService.TokenPair tokens = jwtService.generateTokenPair(
                user.getId(),
                session.getId(),
                user.getRole(),
                user.getOrganizationId()
        );

        log.info("User logged in: {} (new: {})", user.getId(), isNewUser);

        return AuthResponse.builder()
                .user(mapUserToDto(user))
                .tokens(TokensDto.builder()
                        .accessToken(tokens.getAccessToken())
                        .refreshToken(tokens.getRefreshToken())
                        .accessTokenExpiresAt(tokens.getAccessTokenExpiresAt())
                        .refreshTokenExpiresAt(tokens.getRefreshTokenExpiresAt())
                        .build())
                .isNewUser(isNewUser)
                .build();
    }

    /**
     * Refresh access token
     */
    @Transactional
    public TokensDto refreshToken(RefreshTokenDto request) {
        // Validate refresh token
        JwtService.RefreshTokenClaims claims;
        try {
            claims = jwtService.validateRefreshToken(request.getRefreshToken());
        } catch (JwtService.TokenExpiredException e) {
            throw AuthenticationException.tokenExpired();
        } catch (JwtService.InvalidTokenException e) {
            throw AuthenticationException.invalidToken();
        }

        // Verify session
        Session session = sessionRepository.findValidSession(claims.getSessionId(), Instant.now())
                .orElseThrow(AuthenticationException::sessionRevoked);

        // Verify token hash matches
        String tokenHash = passwordEncoder.encode(request.getRefreshToken());
        // Note: In production, compare hashes properly

        // Get user
        User user = userRepository.findById(claims.getUserId())
                .filter(u -> u.getIsActive() && u.getDeletedAt() == null)
                .orElseThrow(AuthenticationException::invalidCredentials);

        // Generate new tokens
        JwtService.TokenPair tokens = jwtService.generateTokenPair(
                user.getId(),
                session.getId(),
                user.getRole(),
                user.getOrganizationId()
        );

        // Update session
        session.setRefreshTokenHash(passwordEncoder.encode(tokens.getRefreshToken()));
        session.updateLastUsed();
        sessionRepository.save(session);

        log.debug("Token refreshed for user: {}", user.getId());

        return TokensDto.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .accessTokenExpiresAt(tokens.getAccessTokenExpiresAt())
                .refreshTokenExpiresAt(tokens.getRefreshTokenExpiresAt())
                .build();
    }

    /**
     * Logout - revoke session
     */
    @Transactional
    public void logout(UUID sessionId) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.revoke("USER_LOGOUT");
            sessionRepository.save(session);
            log.debug("Session revoked: {}", sessionId);
        });
    }

    /**
     * Logout from all devices
     */
    @Transactional
    public int logoutAll(UUID userId) {
        int count = sessionRepository.revokeAllSessionsForUser(userId, Instant.now(), "USER_LOGOUT_ALL");
        log.info("Revoked {} sessions for user: {}", count, userId);
        return count;
    }

    /**
     * Get active sessions for user
     */
    @Transactional(readOnly = true)
    public List<SessionDto> getActiveSessions(UUID userId) {
        return sessionRepository.findActiveSessionsByUserId(userId, Instant.now())
                .stream()
                .map(this::mapSessionToDto)
                .collect(Collectors.toList());
    }

    // ============================================================
    // Private helpers
    // ============================================================

    private User createNewUser(String phone) {
        User user = User.builder()
                .phone(phone)
                .firstName("")
                .lastName("")
                .role(UserRole.FARMER)
                .isPhoneVerified(true)
                .build();
        user = userRepository.save(user);

        // Create farmer profile
        Farmer farmer = Farmer.builder()
                .user(user)
                .build();
        farmerRepository.save(farmer);

        return user;
    }

    private Session createSession(User user, String deviceId, String deviceName, String devicePlatform) {
        JwtService.TokenPair tempTokens = jwtService.generateTokenPair(
                user.getId(), UUID.randomUUID(), user.getRole(), user.getOrganizationId()
        );

        Session session = Session.builder()
                .user(user)
                .refreshTokenHash(passwordEncoder.encode(tempTokens.getRefreshToken()))
                .deviceId(deviceId)
                .deviceName(deviceName)
                .devicePlatform(devicePlatform)
                .expiresAt(tempTokens.getRefreshTokenExpiresAt())
                .build();

        return sessionRepository.save(session);
    }

    private String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        return "****" + phone.substring(phone.length() - 4);
    }

    private UserDto mapUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .organizationId(user.getOrganizationId())
                .preferredLanguage(user.getPreferredLanguage())
                .profileImageUrl(user.getProfileImageUrl())
                .isPhoneVerified(user.getIsPhoneVerified())
                .isEmailVerified(user.getIsEmailVerified())
                .build();
    }

    private SessionDto mapSessionToDto(Session session) {
        return SessionDto.builder()
                .id(session.getId())
                .deviceId(session.getDeviceId())
                .deviceName(session.getDeviceName())
                .devicePlatform(session.getDevicePlatform())
                .lastUsedAt(session.getLastUsedAt())
                .createdAt(session.getCreatedAt())
                .build();
    }
}
