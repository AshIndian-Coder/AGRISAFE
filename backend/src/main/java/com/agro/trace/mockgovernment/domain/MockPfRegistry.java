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

import java.time.Instant;

@Entity
@Table(name = "mock_pf_registry")
@Getter
@Setter
@NoArgsConstructor
public class MockPfRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pf_reference", unique = true, nullable = false, length = 64)
    private String pfReference;

    @Column(name = "identity_reference", nullable = false, length = 64)
    private String identityReference;

    @Column(name = "employee_name", length = 255)
    private String employeeName;

    @Column(name = "organization_reference", length = 64)
    private String organizationReference;

    @Column(name = "designation", length = 255)
    private String designation;

    @Column(name = "employment_status", length = 20)
    private String employmentStatus = "ACTIVE";

    @Column(name = "registered_mobile", length = 16)
    private String registeredMobile;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}