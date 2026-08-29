package com.agro.trace.products.repository;

import com.agro.trace.products.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductCode(String productCode);
    List<Product> findByActiveTrue();
    List<Product> findByCategory(String category);
    List<Product> findByFssaiApplicableTrue();

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findDistinctCategories();
}