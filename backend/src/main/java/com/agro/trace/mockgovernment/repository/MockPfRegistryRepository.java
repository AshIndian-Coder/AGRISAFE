package com.agro.trace.mockgovernment.repository;

import com.agro.trace.mockgovernment.domain.MockPfRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MockPfRegistryRepository extends JpaRepository<MockPfRegistry, Long> {
    Optional<MockPfRegistry> findByPfReference(String pfReference);
    boolean existsByPfReference(String pfReference);
}