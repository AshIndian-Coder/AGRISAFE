package com.agro.trace.mockgovernment.repository;

import com.agro.trace.mockgovernment.domain.MockGovernmentEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MockGovernmentEmployeeRepository extends JpaRepository<MockGovernmentEmployee, Long> {
    Optional<MockGovernmentEmployee> findByEmployeeReference(String employeeReference);
    Optional<MockGovernmentEmployee> findByPfReference(String pfReference);
}