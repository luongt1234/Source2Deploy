package com.source2deploy.event.infrastructure;

import java.time.Instant;
import java.util.UUID;

public record InfrastructureRequestedEvent(
        UUID deploymentId,
        UUID projectId,
        String environment,
        int containerPort,
        Instant requestedAt
) {}
