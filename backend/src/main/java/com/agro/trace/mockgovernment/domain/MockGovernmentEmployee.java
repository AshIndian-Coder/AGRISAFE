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
@Table(name = "mock_government_employees")
@Getter
@Setter
@NoArgsConstructor
public class MockGovernmentEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_reference", unique = true, nullable = false, length = 64)
    private String employeeReference;

    @Column(name = "pf_reference", nullable = false, length = 64)
    private String pfReference;

    @Column(name = "identity_reference", nullable = false, length = 64)
    private String identityReference;

    @Column(length = 255)
    private String department;

    @Column(length = 255)
    private String designation;

    @Column(length = 255)
    private String jurisdiction;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // Transient for employee verification
    public String getPersonName() {
        return designation + " - " + department;
    }

    public String getRegisteredMobile() {
        return "NOT_AVAILABLE";
    }
}