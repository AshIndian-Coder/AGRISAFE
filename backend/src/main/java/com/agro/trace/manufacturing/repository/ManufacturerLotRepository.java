package com.agro.trace.manufacturing.repository;

import com.agro.trace.manufacturing.domain.ManufacturerLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManufacturerLotRepository extends JpaRepository<ManufacturerLot, Long> {
    Optional<ManufacturerLot> findByManufacturerLotId(String manufacturerLotId);
    List<ManufacturerLot> findByManufacturerEmployeeUuid(String employeeUuid);
    List<ManufacturerLot> findByStatus(String status);
}