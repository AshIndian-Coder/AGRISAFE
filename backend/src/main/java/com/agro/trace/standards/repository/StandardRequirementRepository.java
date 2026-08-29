package com.agro.trace.standards.repository;

import com.agro.trace.standards.domain.StandardRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandardRequirementRepository extends JpaRepository<StandardRequirement, Long> {
    List<StandardRequirement> findByProductId(Long productId);
    List<StandardRequirement> findByStandardId(Long standardId);
    List<StandardRequirement> findByProductIdAndActiveTrue(Long productId);
    List<StandardRequirement> findByTestCode(String testCode);
}