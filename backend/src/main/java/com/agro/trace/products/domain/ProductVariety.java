package com.agro.trace.products.domain;

import com.agro.trace.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_varieties")
@Getter
@Setter
@NoArgsConstructor
public class ProductVariety extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "variety_code", nullable = false, length = 32)
    private String varietyCode;

    @Column(name = "variety_name", nullable = false, length = 255)
    private String varietyName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}