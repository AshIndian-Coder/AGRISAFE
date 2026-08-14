package com.agrichain.organization.entity;

import com.agrichain.common.entity.BaseEntity;
import com.agrichain.common.enums.OrganizationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Organization entity - represents businesses in the supply chain
 */
@Entity
@Table(name = "organizations", indexes = {
    @Index(name = "idx_organizations_type", columnList = "type"),
    @Index(name = "idx_organizations_is_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private OrganizationType type;

    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 254)
    private String email;

    @Column(name = "address", columnDefinition = "JSON")
    private String address;

    @Column(name = "location", columnDefinition = "JSON")
    private String location;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    public void verify() {
        this.isVerified = true;
        this.verifiedAt = Instant.now();
    }
}
