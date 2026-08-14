package com.agrichain.batch.entity;

import com.agrichain.identity.entity.User;
import com.agrichain.organization.entity.Organization;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * TraceabilityEvent entity - immutable record of batch lifecycle events
 */
@Entity
@Table(name = "traceability_events", indexes = {
    @Index(name = "idx_traceability_batch", columnList = "batch_id"),
    @Index(name = "idx_traceability_event_type", columnList = "event_type"),
    @Index(name = "idx_traceability_actor", columnList = "actor_id"),
    @Index(name = "idx_traceability_timestamp", columnList = "server_timestamp"),
    @Index(name = "idx_traceability_correlation", columnList = "correlation_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraceabilityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false, updatable = false)
    private Batch batch;

    @Column(name = "event_type", nullable = false, length = 100, updatable = false)
    private String eventType;

    @Column(name = "event_version", nullable = false, updatable = false)
    @Builder.Default
    private Integer eventVersion = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", updatable = false)
    private User actor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_organization_id", updatable = false)
    private Organization actorOrganization;

    @Column(name = "previous_state", length = 50, updatable = false)
    private String previousState;

    @Column(name = "new_state", length = 50, updatable = false)
    private String newState;

    @Column(name = "location", columnDefinition = "JSON", updatable = false)
    private String location;

    @Column(name = "server_timestamp", nullable = false, updatable = false)
    @Builder.Default
    private Instant serverTimestamp = Instant.now();

    @Column(name = "client_timestamp", updatable = false)
    private Instant clientTimestamp;

    @Column(name = "data", columnDefinition = "JSON", updatable = false)
    private String data;

    @Column(name = "metadata", columnDefinition = "JSON", updatable = false)
    private String metadata;

    @Column(name = "correlation_id", length = 128, updatable = false)
    private String correlationId;

    @Column(name = "causation_id", length = 128, updatable = false)
    private String causationId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
