package com.agrichain.farmer.entity;

import com.agrichain.common.entity.BaseEntity;
import com.agrichain.identity.entity.User;
import com.agrichain.organization.entity.Organization;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Farmer entity - farmer profile with agricultural details
 */
@Entity
@Table(name = "farmers", indexes = {
    @Index(name = "idx_farmers_organization", columnList = "organization_id"),
    @Index(name = "idx_farmers_is_verified", columnList = "is_verified")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farmer extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(name = "aadhaar_number_hash", length = 255)
    private String aadhaarNumberHash;

    @Column(name = "farmer_id_number", unique = true, length = 100)
    private String farmerIdNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "address", columnDefinition = "JSON")
    private String address;

    @Column(name = "bank_account_details", columnDefinition = "JSON")
    private String bankAccountDetails;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "verification_method", length = 50)
    private String verificationMethod;

    @Column(name = "total_farms", nullable = false)
    @Builder.Default
    private Integer totalFarms = 0;

    @Column(name = "total_area_hectares", precision = 12, scale = 4)
    @Builder.Default
    private BigDecimal totalAreaHectares = BigDecimal.ZERO;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    public void verify(String method) {
        this.isVerified = true;
        this.verifiedAt = Instant.now();
        this.verificationMethod = method;
    }

    public void addFarm(BigDecimal areaHectares) {
        this.totalFarms++;
        this.totalAreaHectares = this.totalAreaHectares.add(areaHectares);
    }
}
