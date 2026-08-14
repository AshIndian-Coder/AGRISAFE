package com.agrichain.organization.repository;

import com.agrichain.common.enums.OrganizationType;
import com.agrichain.organization.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findByIdAndDeletedAtIsNull(UUID id);

    Page<Organization> findByTypeAndIsActiveAndDeletedAtIsNull(OrganizationType type, Boolean isActive, Pageable pageable);

    Page<Organization> findByIsActiveAndDeletedAtIsNull(Boolean isActive, Pageable pageable);

    Optional<Organization> findByRegistrationNumberAndDeletedAtIsNull(String registrationNumber);

    boolean existsByRegistrationNumberAndDeletedAtIsNull(String registrationNumber);
}
