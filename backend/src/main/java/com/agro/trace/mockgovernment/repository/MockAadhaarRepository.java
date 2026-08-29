package com.agro.trace.mockgovernment.repository;

import com.agro.trace.mockgovernment.domain.MockAadhaarRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MockAadhaarRepository extends JpaRepository<MockAadhaarRegistry, Long> {
    Optional<MockAadhaarRegistry> findByAadhaarReference(String aadhaarReference);
    Optional<MockAadhaarRegistry> findByRegisteredMobile(String mobile);
    boolean existsByAadhaarReference(String aadhaarReference);
}