package com.agro.trace.auth;

import com.agro.trace.auth.dto.FarmerRegistrationRequest;
import com.agro.trace.auth.dto.LoginRequest;
import com.agro.trace.auth.service.AuthService;
import com.agro.trace.common.exception.BusinessException;
import com.agro.trace.common.exception.DuplicateResourceException;
import com.agro.trace.mockgovernment.domain.MockAadhaarRegistry;
import com.agro.trace.mockgovernment.service.GovernmentIdentityProvider;
import com.agro.trace.users.domain.Role;
import com.agro.trace.users.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    private GovernmentIdentityProvider mockGovProvider;

    private MockAadhaarRegistry mockIdentity;

    @BeforeEach
    void setUp() {
        // Ensure farmer role exists
        if (!roleRepository.existsByName("ROLE_FARMER")) {
            roleRepository.save(new Role("ROLE_FARMER", "Farmer"));
        }

        // Setup mock government identity
        mockIdentity = new MockAadhaarRegistry();
        mockIdentity.setAadhaarReference("AADHAR-REF-TEST-001");
        mockIdentity.setMaskedAadhaar("XXXX-XXXX-0001");
        mockIdentity.setRegisteredMobile("9876543210");
        mockIdentity.setPersonName("Test Farmer");
        mockIdentity.setStatus("ACTIVE");
        mockIdentity.setVerificationStatus("VERIFIED");

        when(mockGovProvider.verifyIdentity(anyString()))
                .thenReturn(Optional.of(mockIdentity));
    }

    @Test
    void testFarmerRegistration() {
        var request = new FarmerRegistrationRequest(
                "AADHAR-REF-TEST-001",
                "123456",
                "123456"
        );

        var response = assertDoesNotThrow(() -> authService.registerFarmer(request));
        assertNotNull(response);
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertNotNull(response.userUuid());
        assertEquals("ROLE_FARMER", response.role());
    }

    @Test
    void testDuplicateRegistration() {
        var request = new FarmerRegistrationRequest(
                "AADHAR-REF-TEST-001",
                "123456",
                "123456"
        );

        authService.registerFarmer(request);

        assertThrows(DuplicateResourceException.class, () -> {
            authService.registerFarmer(request);
        });
    }

    @Test
    void testLoginSuccess() {
        var regRequest = new FarmerRegistrationRequest(
                "AADHAR-REF-TEST-001",
                "123456",
                "654321"
        );
        authService.registerFarmer(regRequest);

        var loginRequest = new LoginRequest(
                "AADHAR-REF-TEST-001",
                "654321"
        );

        var response = assertDoesNotThrow(() -> authService.login(loginRequest));
        assertNotNull(response);
        assertNotNull(response.accessToken());
        assertEquals("ROLE_FARMER", response.role());
    }

    @Test
    void testLoginWithWrongPin() {
        var regRequest = new FarmerRegistrationRequest(
                "AADHAR-REF-TEST-001",
                "123456",
                "654321"
        );
        authService.registerFarmer(regRequest);

        var loginRequest = new LoginRequest(
                "AADHAR-REF-TEST-001",
                "000000" // Wrong PIN
        );

        assertThrows(BusinessException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    void testInvalidAadhaarRegistration() {
        // Make the government provider return empty for this test
        when(mockGovProvider.verifyIdentity("INVALID-AADHAAR"))
                .thenReturn(Optional.empty());

        var request = new FarmerRegistrationRequest(
                "INVALID-AADHAAR",
                "123456",
                "123456"
        );

        assertThrows(BusinessException.class, () -> {
            authService.registerFarmer(request);
        });
    }
}