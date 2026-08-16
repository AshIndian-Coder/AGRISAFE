package com.agro.trace.mockgovernment.service;

import com.agro.trace.mockgovernment.domain.MockAadhaarRegistry;
import com.agro.trace.mockgovernment.domain.MockGovernmentEmployee;
import com.agro.trace.mockgovernment.domain.MockPfRegistry;
import com.agro.trace.mockgovernment.repository.MockAadhaarRepository;
import com.agro.trace.mockgovernment.repository.MockGovernmentEmployeeRepository;
import com.agro.trace.mockgovernment.repository.MockPfRegistryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Government Identity Provider - Prototype Mock Implementation.
 * In production, this would connect to real government databases through secure APIs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GovernmentIdentityProvider {

    private final MockAadhaarRepository aadhaarRepository;
    private final MockPfRegistryRepository pfRegistryRepository;
    private final MockGovernmentEmployeeRepository governmentEmployeeRepository;

    /**
     * Verify Aadhaar identity and return identity details.
     */
    public Optional<MockAadhaarRegistry> verifyIdentity(String aadhaarReference) {
        log.debug("Verifying identity: {}", maskSensitive(aadhaarReference));
        return aadhaarRepository.findByAadhaarReference(aadhaarReference)
                .filter(a -> "ACTIVE".equals(a.getStatus()) && "VERIFIED".equals(a.getVerificationStatus()));
    }

    /**
     * Verify registered mobile for Aadhaar.
     */
    public boolean verifyRegisteredMobile(String aadhaarReference, String mobile) {
        return aadhaarRepository.findByAadhaarReference(aadhaarReference)
                .filter(a -> a.getRegisteredMobile().equals(mobile))
                .isPresent();
    }

    /**
     * Verify PF registration linking.
     */
    public Optional<MockPfRegistry> verifyPfRegistration(String pfReference, String aadhaarReference) {
        log.debug("Verifying PF registration: {}", maskSensitive(pfReference));
        return pfRegistryRepository.findByPfReference(pfReference)
                .filter(p -> p.getIdentityReference().equals(aadhaarReference))
                .filter(p -> "ACTIVE".equals(p.getEmploymentStatus()));
    }

    /**
     * Verify employee identity through government employee registry.
     */
    public Optional<MockGovernmentEmployee> verifyEmployee(String employeeId, String aadhaarReference,
                                                           Long organizationId) {
        log.debug("Verifying employee: {} org={}", maskSensitive(employeeId), organizationId);
        var employee = governmentEmployeeRepository.findByEmployeeReference(employeeId);
        if (employee.isPresent()) {
            var emp = employee.get();
            if (emp.getIdentityReference().equals(aadhaarReference) && emp.isActive()) {
                return employee;
            }
        }
        return Optional.empty();
    }

    /**
     * Lookup identity by reference.
     */
    public Optional<MockAadhaarRegistry> findIdentityByReference(String reference) {
        return aadhaarRepository.findByAadhaarReference(reference);
    }

    private String maskSensitive(String value) {
        if (value == null || value.length() < 6) return "***";
        return value.substring(0, 2) + "***" + value.substring(value.length() - 2);
    }
}