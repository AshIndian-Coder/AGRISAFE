package com.agro.trace.users.domain;

import com.agro.trace.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(name = "aadhaar_reference", unique = true, length = 64)
    private String aadhaarReference;

    @Column(name = "masked_aadhaar", length = 16)
    private String maskedAadhaar;

    @Column(name = "pf_reference", unique = true, length = 64)
    private String pfReference;

    @Column(name = "employee_id", length = 64)
    private String employeeId;

    @Column(name = "gst_number", length = 32)
    private String gstNumber;

    @Column(name = "pan_number", length = 16)
    private String panNumber;

    @Column(name = "mobile_number", length = 16)
    private String mobileNumber;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "pin_hash", nullable = false, length = 512)
    private String pinHash;

    @Column(name = "pin_attempts", nullable = false)
    private int pinAttempts = 0;

    @Column(name = "pin_locked_until")
    private Instant pinLockedUntil;

    @Column(name = "otp_hash", length = 512)
    private String otpHash;

    @Column(name = "otp_expires_at")
    private Instant otpExpiresAt;

    @Column(name = "otp_sent_count")
    private int otpSentCount = 0;

    @Column(name = "otp_sent_window_start")
    private Instant otpSentWindowStart;

    @Column(name = "identity_reference", length = 64)
    private String identityReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 32)
    private UserType userType;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "mobile_verified", nullable = false)
    private boolean mobileVerified = false;

    @Column(name = "registration_complete", nullable = false)
    private boolean registrationComplete = false;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "last_login_ip", length = 64)
    private String lastLoginIp;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Organization association
    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "functional_type", length = 32)
    private String functionalType;

    @Column(name = "wallet_address", length = 42)
    private String walletAddress;

    public void incrementPinAttempts() {
        this.pinAttempts++;
    }

    public void resetPinAttempts() {
        this.pinAttempts = 0;
        this.pinLockedUntil = null;
    }

    public boolean isPinLocked() {
        return pinLockedUntil != null && pinLockedUntil.isAfter(Instant.now());
    }
}