package com.agro.trace.auth;

import com.agro.trace.auth.dto.LoginRequest;
import com.agro.trace.auth.dto.SignupRequest;
import com.agro.trace.auth.service.AuthService;
import com.agro.trace.common.exception.BusinessException;
import com.agro.trace.common.exception.DuplicateResourceException;
import com.agro.trace.users.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        if (!roleRepository.existsByName("ROLE_FARMER")) {
            var role = new com.agro.trace.users.domain.Role();
            role.setName("ROLE_FARMER");
            role.setDescription("Farmer");
            roleRepository.save(role);
        }
    }

    private SignupRequest signupRequest(String email) {
        return new SignupRequest(
                email,
                "Test Farmer",
                "FARMER",
                "123456",
                null
        );
    }

    @Test
    void testSignup() {
        var request = signupRequest("farmer@example.com");

        var response = assertDoesNotThrow(() -> authService.signup(request));
        assertNotNull(response);
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertNotNull(response.userUuid());
        assertEquals("ROLE_FARMER", response.role());
    }

    @Test
    void testDuplicateSignup() {
        var request = signupRequest("duplicate@example.com");

        authService.signup(request);

        assertThrows(DuplicateResourceException.class, () -> {
            authService.signup(request);
        });
    }

    @Test
    void testLoginSuccess() {
        authService.signup(signupRequest("login@example.com"));

        var loginRequest = new LoginRequest(
                "login@example.com",
                "123456"
        );

        var response = assertDoesNotThrow(() -> authService.login(loginRequest));
        assertNotNull(response);
        assertNotNull(response.accessToken());
        assertEquals("ROLE_FARMER", response.role());
    }

    @Test
    void testLoginWithWrongPin() {
        authService.signup(signupRequest("wrongpin@example.com"));

        var loginRequest = new LoginRequest(
                "wrongpin@example.com",
                "000000"
        );

        assertThrows(BusinessException.class, () -> {
            authService.login(loginRequest);
        });
    }
}
