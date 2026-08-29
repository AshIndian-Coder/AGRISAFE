package com.agro.trace.auth.service;

import com.agro.trace.auth.domain.EmailOtp;
import com.agro.trace.auth.dto.*;
import com.agro.trace.auth.repository.EmailOtpRepository;
import com.agro.trace.common.exception.*;
import com.agro.trace.notifications.service.NotificationProvider;
import com.agro.trace.security.config.SecurityProperties;
import com.agro.trace.security.service.JwtService;
import com.agro.trace.users.domain.Role;
import com.agro.trace.users.domain.User;
import com.agro.trace.users.domain.UserType;
import com.agro.trace.users.repository.RoleRepository;
import com.agro.trace.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityProperties securityProperties;
    private final NotificationProvider notificationProvider;
    private final EmailOtpRepository emailOtpRepository;
    private final com.agro.trace.auth.repository.RefreshTokenRepository refreshTokenRepository;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Transactional
    public String sendOtp(String email, String purpose) {
        String otpCode = String.format("%06d", SECURE_RANDOM.nextInt(999999));
        emailOtpRepository.deleteByPurpose(email, purpose);

        EmailOtp otpEntity = EmailOtp.builder()
                .email(email.toLowerCase().trim())
                .otpCode(otpCode)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .verified(false)
                .attemptCount(0)
                .purpose(purpose)
                .build();
        emailOtpRepository.save(otpEntity);

        String subject = purpose.equals("SIGNUP") ? "AgriSafe - Verify Your Email" : "AgriSafe - Login OTP";
        String body = "Your verification code is: " + otpCode + "\n\nThis code expires in 5 minutes.\nDo not share this code with anyone.";
        notificationProvider.sendEmail(email, subject, body);
        log.info("OTP sent to {} for purpose {} - code: {}", email, purpose, otpCode);
        return otpCode;
    }

    @Transactional
    public boolean verifyOtp(String email, String otp, String purpose) {
        Optional<EmailOtp> latestOpt = emailOtpRepository.findLatestUnverified(email.toLowerCase().trim(), purpose);
        if (latestOpt.isEmpty()) {
            throw new BusinessException("OTP_NOT_FOUND", "No OTP found. Please request a new one.", 400);
        }
        EmailOtp otpEntity = latestOpt.get();
        if (otpEntity.isExpired()) {
            throw new BusinessException("OTP_EXPIRED", "OTP has expired. Please request a new one.", 400);
        }
        if (otpEntity.getAttemptCount() >= 5) {
            throw new BusinessException("OTP_MAX_ATTEMPTS", "Too many failed attempts. Please request a new OTP.", 429);
        }
        if (!otpEntity.getOtpCode().equals(otp)) {
            otpEntity.setAttemptCount(otpEntity.getAttemptCount() + 1);
            emailOtpRepository.save(otpEntity);
            int remaining = 5 - otpEntity.getAttemptCount();
            throw new BusinessException("OTP_INVALID", "Invalid OTP. " + remaining + " attempts remaining.", 400);
        }
        otpEntity.setVerified(true);
        emailOtpRepository.save(otpEntity);
        log.info("OTP verified for {} purpose={}", email, purpose);
        return true;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        String email = request.email().toLowerCase().trim();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("DUPLICATE_EMAIL", "Email already registered. Please sign in.");
        }

        String roleName = switch (request.userType()) {
            case "FARMER" -> "ROLE_FARMER";
            case "COLLECTING_AGENT" -> "ROLE_COLLECTING_AGENT";
            case "TESTING_AGENT" -> "ROLE_TESTING_AGENT";
            case "SUPPLIER" -> "ROLE_SUPPLIER";
            case "MANUFACTURER_EMPLOYEE" -> "ROLE_MANUFACTURER_EMPLOYEE";
            case "DISTRIBUTOR_EMPLOYEE" -> "ROLE_DISTRIBUTOR_EMPLOYEE";
            case "RETAILER" -> "ROLE_RETAILER";
            case "GOVERNMENT_EMPLOYEE" -> "ROLE_GOVERNMENT_EMPLOYEE";
            case "GOVERNMENT_INVESTIGATOR" -> "ROLE_GOVERNMENT_INVESTIGATOR";
            default -> throw new BusinessException("INVALID_USER_TYPE", "Invalid user type: " + request.userType());
        };

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role", roleName));

        User user = new User();
        user.setEmail(email);
        user.setName(request.name());
        user.setPinHash(passwordEncoder.encode(request.pin()));
        user.setUserType(UserType.valueOf(request.userType()));
        user.setIdentityReference(email);
        user.setWalletAddress(request.walletAddress());
        user.setRegistrationComplete(true);
        user.setMobileVerified(true);
        user.setEmailVerified(true);
        user.getRoles().add(role);
        user = userRepository.save(user);

        log.info("User signed up: {} email={} type={}", user.getUuid(), email, request.userType());
        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.email().toLowerCase().trim();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("No account found with this email. Please sign up first."));

        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }
        if (user.isPinLocked()) {
            throw new BusinessException("PIN_LOCKED",
                    "Account is locked due to too many PIN attempts. Try again after "
                            + securityProperties.getPinLockoutDurationMinutes() + " minutes.", 423);
        }
        if (!passwordEncoder.matches(request.pin(), user.getPinHash())) {
            user.incrementPinAttempts();
            if (user.getPinAttempts() >= securityProperties.getPinAttemptLimit()) {
                user.setPinLockedUntil(Instant.now().plus(
                        securityProperties.getPinLockoutDurationMinutes(), ChronoUnit.MINUTES));
                userRepository.save(user);
                throw new BusinessException("PIN_LOCKED",
                        "Account locked due to too many failed PIN attempts", 423);
            }
            userRepository.save(user);
            throw new BusinessException("PIN_INVALID",
                    "Invalid PIN. " + (securityProperties.getPinAttemptLimit() - user.getPinAttempts()) + " attempts remaining", 401);
        }

        user.resetPinAttempts();
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
        log.info("User logged in: {} email={}", user.getUuid(), email);
        return generateAuthResponse(user);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim()).isPresent();
    }

    @Transactional
    public AuthResponse unlockApp(String userUuid, String pin) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", userUuid));
        if (!user.isActive()) throw new UnauthorizedException("Account is deactivated");
        if (user.isPinLocked()) throw new BusinessException("PIN_LOCKED", "Account is locked", 423);
        if (!passwordEncoder.matches(pin, user.getPinHash())) {
            user.incrementPinAttempts();
            userRepository.save(user);
            throw new BusinessException("PIN_INVALID", "Invalid PIN", 401);
        }
        user.resetPinAttempts();
        userRepository.save(user);
        return generateAuthResponse(user);
    }

    @Transactional
    public void logout(String userUuid) {
        refreshTokenRepository.deleteByUserUuid(userUuid);
        log.info("User logged out, all refresh tokens revoked: {}", userUuid);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new BusinessException("REFRESH_TOKEN_EXPIRED", "Refresh token has expired", 401);
        }
        var storedToken = refreshTokenRepository.findByToken(refreshToken);
        if (storedToken.isEmpty() || storedToken.get().isRevoked()) {
            throw new BusinessException("REFRESH_TOKEN_REVOKED", "Refresh token has been revoked. Please login again.", 401);
        }
        String userUuid = jwtService.extractSubject(refreshToken);
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", userUuid));
        return generateAuthResponse(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getUuid(), user.getPinHash(),
                user.getRoles().stream()
                        .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority(r.getName()))
                        .toList());

        String accessToken = jwtService.generateAccessToken(
                Map.of("role", user.getRoles().iterator().next().getName(),
                       "userType", user.getUserType().name()),
                userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        com.agro.trace.auth.domain.RefreshToken tokenEntity = new com.agro.trace.auth.domain.RefreshToken();
        tokenEntity.setToken(refreshToken);
        tokenEntity.setUserUuid(user.getUuid());
        tokenEntity.setExpiresAt(java.time.Instant.now().plusSeconds(2592000));
        refreshTokenRepository.save(tokenEntity);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(900)
                .userUuid(user.getUuid())
                .userName(user.getName())
                .userType(user.getUserType().name())
                .role(user.getRoles().iterator().next().getName())
                .organizationId(user.getOrganizationId())
                .build();
    }
}
