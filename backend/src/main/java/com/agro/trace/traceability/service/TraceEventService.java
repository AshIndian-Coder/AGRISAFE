package com.agro.trace.traceability.service;

import com.agro.trace.common.domain.ActionType;
import com.agro.trace.common.dto.PagedResponse;
import com.agro.trace.traceability.domain.TraceEvent;
import com.agro.trace.traceability.repository.TraceEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TraceEventService {

    private final TraceEventRepository traceEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public TraceEvent recordEvent(
            ActionType action,
            String objectType,
            String objectId,
            String actorUuid,
            String actorRole,
            String previousState,
            String newState,
            BigDecimal latitude,
            BigDecimal longitude,
            String qrId,
            String metadataJson) {

        String eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        TraceEvent event = new TraceEvent();
        event.setEventId(eventId);
        event.setObjectType(objectType);
        event.setObjectId(objectId);
        event.setActorUuid(actorUuid);
        event.setActorRole(actorRole);
        event.setAction(action);
        event.setPreviousState(previousState);
        event.setNewState(newState);
        event.setEventTimestamp(Instant.now());
        event.setLatitude(latitude);
        event.setLongitude(longitude);
        event.setQrId(qrId);
        event.setMetadataJson(metadataJson);
        event.setTraceId(MDC.get("correlationId"));

        // Generate event hash for integrity
        event.setEventHash(generateEventHash(event));

        event = traceEventRepository.save(event);

        log.debug("Trace event recorded: {} - {}:{} -> {}", eventId, objectType, objectId, action);
        return event;
    }

    public PagedResponse<TraceEvent> getObjectTrace(String objectType, String objectId, Pageable pageable) {
        Page<TraceEvent> events = traceEventRepository
                .findByObjectTypeAndObjectIdOrderByEventTimestampDesc(objectType, objectId, pageable);
        return PagedResponse.from(events);
    }

    public List<TraceEvent> getObjectTraceAll(String objectType, String objectId) {
        return traceEventRepository.findByObjectTypeAndObjectIdOrderByEventTimestampAsc(objectType, objectId);
    }

    public List<TraceEvent> getCustodyWindowTrace(String objectType, String objectId, String viewerUuid) {
        List<TraceEvent> allEvents = traceEventRepository
                .findByObjectTypeAndObjectIdOrderByEventTimestampAsc(objectType, objectId);

        if (allEvents.isEmpty()) return allEvents;

        int cutoffIndex = -1;
        for (int i = allEvents.size() - 1; i >= 0; i--) {
            if (viewerUuid.equals(allEvents.get(i).getActorUuid())) {
                cutoffIndex = i;
                break;
            }
        }

        if (cutoffIndex == -1) {
            return allEvents.stream()
                    .filter(e -> e.getAction() == com.agro.trace.common.domain.ActionType.LOT_CREATED)
                    .toList();
        }

        return allEvents.subList(0, cutoffIndex + 1);
    }

    public PagedResponse<TraceEvent> getActorTrace(String actorUuid, Pageable pageable) {
        Page<TraceEvent> events = traceEventRepository.findByActorUuidOrderByEventTimestampDesc(actorUuid, pageable);
        return PagedResponse.from(events);
    }

    private String generateEventHash(TraceEvent event) {
        try {
            String serialized = event.getEventId() + "|" +
                    event.getObjectType() + "|" +
                    event.getObjectId() + "|" +
                    event.getActorUuid() + "|" +
                    event.getAction() + "|" +
                    event.getEventTimestamp();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(serialized.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 not available", e);
            return UUID.randomUUID().toString();
        }
    }
}