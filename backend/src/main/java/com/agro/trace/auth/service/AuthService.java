package com.agro.trace.auth.service;

import com.agro.trace.auth.dto.*;
import com.agro.trace.common.exception.*;
import com.agro.trace.mockgovernment.service.GovernmentIdentityProvider;
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
    private final GovernmentIdentityProvider governmentIdentityProvider;
    private final com.agro.trace.auth.repository.RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public AuthResponse registerFarmer(FarmerRegistrationRequest request) {
        // Verify identity through government provider
        var identity = governmentIdentityProvider.verifyIdentity(request.aadhaarReference());
        if (identity.isEmpty()) {
            throw new BusinessException("REG_INVALID_AADHAAR", "Aadhaar verification failed", 400);
        }

        // Check duplicate
        if (userRepository.existsByAadhaarReference(request.aadhaarReference())) {
            throw new DuplicateResourceException("DUPLICATE_AADHAAR", "Aadhaar already registered");
        }

        // Verify OTP (mock - in production would call OtpProvider)
        if (!verifyOtp(identity.get().getRegisteredMobile(), request.otp())) {
            throw new BusinessException("OTP_INVALID", "Invalid OTP", 400);
        }

        // Find role
        Role role = roleRepository.findByName("ROLE_FARMER")
                .orElseThrow(() -> new EntityNotFoundException("Role", "ROLE_FARMER"));

        // Create user
        User user = new User();
        user.setAadhaarReference(request.aadhaarReference());
        user.setMaskedAadhaar(maskAadhaar(request.aadhaarReference()));
        user.setName(identity.get().getPersonName());
        user.setMobileNumber(identity.get().getRegisteredMobile());
        user.setPinHash(passwordEncoder.encode(request.pin()));
        user.setUserType(UserType.FARMER);
        user.setIdentityReference(request.aadhaarReference());
        user.setRegistrationComplete(true);
        user.setMobileVerified(true);
        user.getRoles().add(role);

        user = userRepository.save(user);

        log.info("Farmer registered: {}", user.getUuid());

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse registerPfUser(PfRegistrationRequest request) {
        // Verify PF & identity through government provider
        var pfRecord = governmentIdentityProvider.verifyPfRegistration(
                request.pfReference(), request.aadhaarReference());
        if (pfRecord.isEmpty()) {
            throw new BusinessException("REG_INVALID_PF", "PF/Aadhaar verification failed", 400);
        }

        if (userRepository.existsByPfReference(request.pfReference())) {
            throw new DuplicateResourceException("DUPLICATE_PF", "PF ID already registered");
        }

        if (userRepository.existsByAadhaarReference(request.aadhaarReference())) {
            throw new DuplicateResourceException("DUPLICATE_AADHAAR", "Aadhaar already registered");
        }

        if (!verifyOtp(pfRecord.get().getRegisteredMobile(), request.otp())) {
            throw new BusinessException("OTP_INVALID", "Invalid OTP", 400);
        }

        // Determine role based on user type
        String roleName = switch (request.userType()) {
            case "COLLECTING_AGENT" -> "ROLE_COLLECTING_AGENT";
            case "TESTING_AGENT" -> "ROLE_TESTING_AGENT";
            case "SUPPLIER" -> "ROLE_SUPPLIER";
            case "GOVERNMENT_EMPLOYEE" -> "ROLE_GOVERNMENT_EMPLOYEE";
            case "GOVERNMENT_INVESTIGATOR" -> "ROLE_GOVERNMENT_INVESTIGATOR";
            default -> throw new BusinessException("INVALID_USER_TYPE", "Invalid user type: " + request.userType());
        };

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role", roleName));

        User user = new User();
        user.setAadhaarReference(request.aadhaarReference());
        user.setMaskedAadhaar(maskAadhaar(request.aadhaarReference()));
        user.setPfReference(request.pfReference());
        user.setName(pfRecord.get().getEmployeeName());
        user.setMobileNumber(pfRecord.get().getRegisteredMobile());
        user.setPinHash(passwordEncoder.encode(request.pin()));
        user.setUserType(UserType.valueOf(request.userType()));
        user.setIdentityReference(request.aadhaarReference());
        user.setRegistrationComplete(true);
        user.setMobileVerified(true);
        user.setFunctionalType(request.functionalType());
        user.getRoles().add(role);

        user = userRepository.save(user);

        log.info("PF user registered: {} type={}", user.getUuid(), request.userType());

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse registerEmployee(EmployeeRegistrationRequest request) {
        // Verify employee through government provider
        var employee = governmentIdentityProvider.verifyEmployee(
                request.employeeId(), request.aadhaarReference(), request.organizationId());
        if (employee.isEmpty()) {
            throw new BusinessException("REG_EMPLOYEE_FAILED", "Employee verification failed", 400);
        }

        if (userRepository.existsByEmployeeId(request.employeeId())) {
            throw new DuplicateResourceException("DUPLICATE_EMPLOYEE_ID", "Employee ID already registered");
        }

        if (!verifyOtp(employee.get().getRegisteredMobile(), request.otp())) {
            throw new BusinessException("OTP_INVALID", "Invalid OTP", 400);
        }

        String roleName = switch (request.userType()) {
            case "MANUFACTURER_EMPLOYEE" -> "ROLE_MANUFACTURER_EMPLOYEE";
            case "DISTRIBUTOR_EMPLOYEE" -> "ROLE_DISTRIBUTOR_EMPLOYEE";
            default -> throw new BusinessException("INVALID_USER_TYPE", "Invalid employee type");
        };

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role", roleName));

        User user = new User();
        user.setAadhaarReference(request.aadhaarReference());
        user.setMaskedAadhaar(maskAadhaar(request.aadhaarReference()));
        user.setEmployeeId(request.employeeId());
        user.setName(employee.get().getPersonName());
        user.setMobileNumber(employee.get().getRegisteredMobile());
        user.setPinHash(passwordEncoder.encode(request.pin()));
        user.setUserType(UserType.valueOf(request.userType()));
        user.setIdentityReference(request.aadhaarReference());
        user.setOrganizationId(request.organizationId());
        user.setFunctionalType(request.functionalType());
        user.setRegistrationComplete(true);
        user.setMobileVerified(true);
        user.getRoles().add(role);

        user = userRepository.save(user);

        log.info("Employee registered: {} org={}", user.getUuid(), request.organizationId());

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse registerRetailer(RetailerRegistrationRequest request) {
        // Verify GST & identity
        var identity = governmentIdentityProvider.verifyIdentity(request.aadhaarReference());
        if (identity.isEmpty()) {
            throw new BusinessException("REG_INVALID_AADHAAR", "Aadhaar verification failed", 400);
        }

        if (userRepository.existsByGstNumber(request.gstNumber())) {
            throw new DuplicateResourceException("DUPLICATE_GST", "GST number already registered");
        }

        if (!verifyOtp(identity.get().getRegisteredMobile(), request.otp())) {
            throw new BusinessException("OTP_INVALID", "Invalid OTP", 400);
        }

        Role role = roleRepository.findByName("ROLE_RETAILER")
                .orElseThrow(() -> new EntityNotFoundException("Role", "ROLE_RETAILER"));

        User user = new User();
        user.setAadhaarReference(request.aadhaarReference());
        user.setMaskedAadhaar(maskAadhaar(request.aadhaarReference()));
        user.setGstNumber(request.gstNumber());
        user.setName(identity.get().getPersonName());
        user.setMobileNumber(identity.get().getRegisteredMobile());
        user.setPinHash(passwordEncoder.encode(request.pin()));
        user.setUserType(UserType.RETAILER);
        user.setIdentityReference(request.aadhaarReference());
        user.setRegistrationComplete(true);
        user.setMobileVerified(true);
        user.getRoles().add(role);

        user = userRepository.save(user);

        log.info("Retailer registered: {}", user.getUuid());

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByIdentity(request.identity())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

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

        // Reset attempts on successful login
        user.resetPinAttempts();
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        log.info("User logged in: {}", user.getUuid());

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse unlockApp(String userUuid, String pin) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", userUuid));

        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        if (user.isPinLocked()) {
            throw new BusinessException("PIN_LOCKED", "Account is locked", 423);
        }

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
        // Check JWT expiry first
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new BusinessException("REFRESH_TOKEN_EXPIRED", "Refresh token has expired", 401);
        }

        // Check if token was revoked in database
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

        // Store refresh token in database (for revocation tracking)
        com.agro.trace.auth.domain.RefreshToken tokenEntity = new com.agro.trace.auth.domain.RefreshToken();
        tokenEntity.setToken(refreshToken);
        tokenEntity.setUserUuid(user.getUuid());
        tokenEntity.setExpiresAt(java.time.Instant.now().plusSeconds(2592000)); // 30 days
        refreshTokenRepository.save(tokenEntity);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(900) // 15 minutes
                .userUuid(user.getUuid())
                .userName(user.getName())
                .userType(user.getUserType().name())
                .role(user.getRoles().iterator().next().getName())
                .organizationId(user.getOrganizationId())
                .build();
    }

    private boolean verifyOtp(String mobile, String otp) {
        // Mock OTP verification - in production would call OtpProvider
        return otp != null && otp.length() == 6;
    }

    private String maskAadhaar(String aadhaarRef) {
        if (aadhaarRef == null || aadhaarRef.length() < 8) return "XXXX-XXXX-XXXX";
        String last4 = aadhaarRef.substring(Math.max(0, aadhaarRef.length() - 4));
        return "XXXX-XXXX-" + last4;
    }
}