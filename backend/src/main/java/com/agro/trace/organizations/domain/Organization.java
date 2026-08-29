package com.agro.trace.organizations.domain;

import com.agro.trace.common.domain.BaseEntity;
import com.agro.trace.common.domain.OrganizationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
public class Organization extends BaseEntity {

    @Column(name = "organization_id", unique = true, nullable = false, length = 64)
    private String organizationId;

    @Column(name = "legal_name", nullable = false, length = 255)
    private String legalName;

    @Column(name = "trade_name", length = 255)
    private String tradeName;

    @Column(name = "gst_number", unique = true, length = 32)
    private String gstNumber;

    @Column(name = "pan_number", unique = true, length = 16)
    private String panNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OrganizationStatus status = OrganizationStatus.PENDING;

    @Column(name = "registration_reference", unique = true, length = 64)
    private String registrationReference;

    @Column(name = "org_type", nullable = false, length = 32)
    private String orgType; // MANUFACTURER, DISTRIBUTOR, RETAILER, SUPPLIER, NODAL_CENTER

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "pincode", length = 10)
    private String pincode;

    @Column(name = "country", length = 100)
    private String country = "India";

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Column(name = "approved_by", length = 36)
    private String approvedBy;

    @Column(name = "approved_at")
    private java.time.Instant approvedAt;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;
}