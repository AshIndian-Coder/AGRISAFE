package com.agro.trace.products.repository;

import com.agro.trace.products.domain.ProductVariety;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVarietyRepository extends JpaRepository<ProductVariety, Long> {
    List<ProductVariety> findByProductIdAndActiveTrue(Long productId);
    List<ProductVariety> findByProductId(Long productId);
}