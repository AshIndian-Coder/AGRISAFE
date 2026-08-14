package com.agrichain.product.entity;

import com.agrichain.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Product entity - represents agricultural products in the catalog
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_products_category", columnList = "category"),
    @Index(name = "idx_products_is_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "subcategory", length = 100)
    private String subcategory;

    @Column(name = "variety", length = 100)
    private String variety;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "hs_code", length = 20)
    private String hsCode;

    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    @Column(name = "storage_requirements", columnDefinition = "TEXT")
    private String storageRequirements;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;
}
