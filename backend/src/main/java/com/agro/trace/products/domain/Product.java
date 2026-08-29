package com.agro.trace.products.domain;

import com.agro.trace.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {

    @Column(name = "product_code", unique = true, nullable = false, length = 32)
    private String productCode;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "sub_category", length = 100)
    private String subCategory;

    @Column(name = "default_unit", length = 20)
    private String defaultUnit;

    @Column(name = "requires_packaging", nullable = false)
    private boolean requiresPackaging = true;

    @Column(name = "requires_manufacturing", nullable = false)
    private boolean requiresManufacturing = false;

    @Column(name = "fssai_applicable", nullable = false)
    private boolean fssaiApplicable = true;

    @Column(name = "regulatory_standard_type", length = 50)
    private String regulatoryStandardType = "FSSAI";

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}