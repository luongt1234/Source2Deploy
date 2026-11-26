package com.source2deploy.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Standard Envelope for all Domain Events in Source2Deploy.
 *
 * @param <T> payload type
 * @param eventId unique event identifier
 * @param eventType name of the event type (e.g., "DeploymentStartedEvent")
 * @param aggregateId ID of the domain aggregate root (e.g., deploymentId, projectId)
 * @param correlationId correlation ID tracing the full end-to-end workflow
 * @param occurredAt timestamp when the event occurred
 * @param version schema version of the event
 * @param payload event-specific payload data
 */
public record DomainEvent<T>(
        UUID eventId,
        String eventType,
        UUID aggregateId,
        UUID correlationId,
        Instant occurredAt,
        int version,
        T payload
) {
    public static <T> DomainEvent<T> of(String eventType, UUID aggregateId, UUID correlationId, T payload) {
        return new DomainEvent<>(
                UUID.randomUUID(),
                eventType,
                aggregateId,
                correlationId != null ? correlationId : UUID.randomUUID(),
                Instant.now(),
                1,
                payload
        );
    }
}
