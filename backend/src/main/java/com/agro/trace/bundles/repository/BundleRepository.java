package com.agro.trace.bundles.repository;

import com.agro.trace.bundles.domain.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BundleRepository extends JpaRepository<Bundle, Long> {
    Optional<Bundle> findByBundleId(String bundleId);
    List<Bundle> findByManufacturerLotId(String manufacturerLotId);
    List<Bundle> findByCurrentCustodianUuid(String custodianUuid);
    List<Bundle> findByRetailerUuid(String retailerUuid);
}