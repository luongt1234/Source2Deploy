package com.source2deploy.event.infrastructure;

import java.time.Instant;
import java.util.UUID;

public record InfrastructureReadyEvent(
        UUID deploymentId,
        UUID projectId,
        String ecsClusterArn,
        String targetGroupArn,
        String albDnsName,
        Instant readyAt
) {}
