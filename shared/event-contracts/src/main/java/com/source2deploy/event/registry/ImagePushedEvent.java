package com.source2deploy.event.registry;

import java.time.Instant;
import java.util.UUID;

public record ImagePushedEvent(
        UUID deploymentId,
        UUID projectId,
        String repositoryUri,
        String imageDigest,
        String fullImageUrl,
        Instant pushedAt
) {}
