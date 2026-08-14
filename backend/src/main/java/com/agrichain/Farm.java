package com.agrichain.farm.entity;

import com.agrichain.common.entity.BaseEntity;
import com.agrichain.farmer.entity.Farmer;
import com.agrichain.organization.entity.Organization;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Farm entity - represents agricultural land
 */
@Entity
@Table(name = "farms", indexes = {
    @Index(name = "idx_farms_farmer", columnList = "farmer_id"),
    @Index(name = "idx_farms_organization", columnList = "organization_id"),
    @Index(name = "idx_farms_is_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farm extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "area_hectares", nullable = false, precision = 12, scale = 4)
    private BigDecimal areaHectares;

    @Column(name = "location", nullable = false, columnDefinition = "JSON")
    private String location;

    @Column(name = "address", nullable = false, columnDefinition = "JSON")
    private String address;

    @Column(name = "soil_type", length = 100)
    private String soilType;

    @Column(name = "water_source", length = 50)
    private String waterSource;

    @Column(name = "certifications", columnDefinition = "JSON")
    private String certifications;

    @Column(name = "khasra_number", length = 100)
    private String khasraNumber;

    @Column(name = "land_record_id", length = 100)
    private String landRecordId;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    public void verify() {
        this.isVerified = true;
        this.verifiedAt = Instant.now();
    }
}
