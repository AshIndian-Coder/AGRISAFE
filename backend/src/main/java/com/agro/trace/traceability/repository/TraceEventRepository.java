package com.agro.trace.traceability.repository;

import com.agro.trace.traceability.domain.TraceEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraceEventRepository extends JpaRepository<TraceEvent, Long> {
    Page<TraceEvent> findByObjectTypeAndObjectIdOrderByEventTimestampDesc(String objectType, String objectId, Pageable pageable);
    List<TraceEvent> findByObjectTypeAndObjectIdOrderByEventTimestampAsc(String objectType, String objectId);
    Page<TraceEvent> findByActorUuidOrderByEventTimestampDesc(String actorUuid, Pageable pageable);
    List<TraceEvent> findByObjectTypeAndObjectId(String objectType, String objectId);
}