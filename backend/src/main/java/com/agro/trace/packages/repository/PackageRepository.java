package com.agro.trace.packages.repository;

import com.agro.trace.packages.domain.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    Optional<Package> findByPackageId(String packageId);
    List<Package> findByLotId(String lotId);
    List<Package> findByCurrentCustodianUuid(String custodianUuid);
    List<Package> findByLotIdAndStatus(String lotId, String status);
}