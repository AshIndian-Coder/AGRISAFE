package com.agrichain.batch.repository;

import com.agrichain.batch.entity.TraceabilityEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TraceabilityEventRepository extends JpaRepository<TraceabilityEvent, UUID> {

    @Query("SELECT e FROM TraceabilityEvent e " +
           "LEFT JOIN FETCH e.actor " +
           "LEFT JOIN FETCH e.actorOrganization " +
           "WHERE e.batch.id = :batchId " +
           "ORDER BY e.serverTimestamp ASC")
    List<TraceabilityEvent> findByBatchIdOrderByTimestamp(UUID batchId);

    Page<TraceabilityEvent> findByBatchId(UUID batchId, Pageable pageable);

    List<TraceabilityEvent> findByCorrelationId(String correlationId);
}
