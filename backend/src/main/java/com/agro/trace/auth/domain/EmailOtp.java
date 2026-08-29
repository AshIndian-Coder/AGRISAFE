package com.agro.trace.auth.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "email_otps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 10)
    private String otpCode;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private int attemptCount;

    @Column(length = 32)
    private String purpose; // LOGIN, SIGNUP

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !verified && !isExpired() && attemptCount < 5;
    }
}
