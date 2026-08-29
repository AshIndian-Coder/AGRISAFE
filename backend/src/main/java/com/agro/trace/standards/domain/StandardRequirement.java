package com.agro.trace.standards.domain;

import com.agro.trace.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "standard_requirements")
@Getter
@Setter
@NoArgsConstructor
public class StandardRequirement extends BaseEntity {

    @Column(name = "standard_id", nullable = false)
    private Long standardId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "test_code", length = 50)
    private String testCode;

    @Column(name = "test_name", nullable = false, length = 255)
    private String testName;

    @Column(name = "parameter", length = 255)
    private String parameter;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "minimum_value", precision = 15, scale = 6)
    private BigDecimal minimumValue;

    @Column(name = "maximum_value", precision = 15, scale = 6)
    private BigDecimal maximumValue;

    @Column(name = "allowed_values", length = 1000)
    private String allowedValues;

    @Column(name = "mandatory", nullable = false)
    private boolean mandatory = true;

    @Column(name = "test_method_reference", length = 500)
    private String testMethodReference;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}