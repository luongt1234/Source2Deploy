package com.source2deploy.event.deployment;

import java.time.Instant;
import java.util.UUID;

public record DeploymentCompletedEvent(
        UUID deploymentId,
        UUID projectId,
        String liveUrl,
        Instant completedAt
) {}
