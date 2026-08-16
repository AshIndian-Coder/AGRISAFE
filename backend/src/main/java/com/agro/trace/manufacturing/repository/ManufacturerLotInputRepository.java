package com.agro.trace.manufacturing.repository;

import com.agro.trace.manufacturing.domain.ManufacturerLotInput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManufacturerLotInputRepository extends JpaRepository<ManufacturerLotInput, Long> {
    List<ManufacturerLotInput> findByManufacturerLotId(String manufacturerLotId);
    List<ManufacturerLotInput> findByInputLotId(String inputLotId);
}