package com.agro.trace.products.controller;

import com.agro.trace.common.dto.ApiResponse;
import com.agro.trace.products.domain.Product;
import com.agro.trace.products.domain.ProductVariety;
import com.agro.trace.products.repository.ProductRepository;
import com.agro.trace.products.repository.ProductVarietyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductVarietyRepository productVarietyRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        var products = productRepository.findByActiveTrue();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long productId) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping("/{productId}/varieties")
    public ResponseEntity<ApiResponse<List<ProductVariety>>> getProductVarieties(@PathVariable Long productId) {
        var varieties = productVarietyRepository.findByProductIdAndActiveTrue(productId);
        return ResponseEntity.ok(ApiResponse.success(varieties));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        var categories = productRepository.findDistinctCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}