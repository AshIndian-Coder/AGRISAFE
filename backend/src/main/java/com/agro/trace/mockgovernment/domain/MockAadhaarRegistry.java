package com.agro.trace.mockgovernment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "mock_aadhaar_registry")
@Getter
@Setter
@NoArgsConstructor
public class MockAadhaarRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aadhaar_reference", unique = true, nullable = false, length = 64)
    private String aadhaarReference;

    @Column(name = "masked_aadhaar", length = 16)
    private String maskedAadhaar;

    @Column(name = "registered_mobile", length = 16)
    private String registeredMobile;

    @Column(name = "person_name", length = 255)
    private String personName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @Column(name = "verification_status", length = 20)
    private String verificationStatus = "VERIFIED";

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}