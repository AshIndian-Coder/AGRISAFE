package com.agro.trace.fraud.repository;

import com.agro.trace.fraud.domain.Flag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlagRepository extends JpaRepository<Flag, Long> {
    Page<Flag> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Flag> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    List<Flag> findByEntityTypeAndEntityId(String entityType, String entityId);
    List<Flag> findByFlagType(String flagType);
}