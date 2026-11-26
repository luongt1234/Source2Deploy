package com.source2deploy.event.deploy;

import java.time.Instant;
import java.util.UUID;

public record ApplicationDeployRequestedEvent(
        UUID deploymentId,
        UUID projectId,
        String imageUrl,
        String ecsClusterArn,
        String targetGroupArn,
        int containerPort,
        Instant requestedAt
) {}
