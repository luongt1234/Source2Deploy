package com.source2deploy.deploymentservice.application.command;

import java.util.Map;
import java.util.UUID;

public record CreateDeploymentCommand(
        UUID projectId,
        String repositoryUrl,
        String branch,
        String commitHash,
        Map<String, String> environmentVariables
) {}
