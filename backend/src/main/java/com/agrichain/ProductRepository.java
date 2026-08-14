package com.agrichain.product.repository;

import com.agrichain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByIdAndDeletedAtIsNull(UUID id);

    Page<Product> findByIsActiveAndDeletedAtIsNull(Boolean isActive, Pageable pageable);

    Page<Product> findByCategoryAndIsActiveAndDeletedAtIsNull(String category, Boolean isActive, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.isActive = true " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Product> findWithFilters(String category, String search, Pageable pageable);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isActive = true AND p.deletedAt IS NULL ORDER BY p.category")
    List<String> findDistinctCategories();

    boolean existsByNameAndCategoryAndDeletedAtIsNull(String name, String category);
}
