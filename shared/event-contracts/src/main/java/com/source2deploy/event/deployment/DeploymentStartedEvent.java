package com.source2deploy.event.deployment;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record DeploymentStartedEvent(
        UUID deploymentId,
        UUID projectId,
        String repositoryUrl,
        String branch,
        String commitHash,
        Map<String, String> environmentVariables,
        Instant startedAt
) {}
