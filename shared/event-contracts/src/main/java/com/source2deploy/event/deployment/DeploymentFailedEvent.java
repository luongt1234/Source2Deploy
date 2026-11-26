package com.source2deploy.event.deployment;

import java.time.Instant;
import java.util.UUID;

public record DeploymentFailedEvent(
        UUID deploymentId,
        UUID projectId,
        String failedStage,
        String failureReason,
        Instant failedAt
) {}
