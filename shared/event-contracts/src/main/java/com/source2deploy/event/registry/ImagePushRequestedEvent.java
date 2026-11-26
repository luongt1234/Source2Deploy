package com.source2deploy.event.registry;

import java.time.Instant;
import java.util.UUID;

public record ImagePushRequestedEvent(
        UUID deploymentId,
        UUID projectId,
        String localImageTag,
        String repositoryName,
        Instant requestedAt
) {}
