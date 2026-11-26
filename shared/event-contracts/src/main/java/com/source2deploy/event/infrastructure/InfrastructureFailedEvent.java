package com.source2deploy.event.infrastructure;

import java.time.Instant;
import java.util.UUID;

public record InfrastructureFailedEvent(
        UUID deploymentId,
        UUID projectId,
        String reason,
        Instant failedAt
) {}
