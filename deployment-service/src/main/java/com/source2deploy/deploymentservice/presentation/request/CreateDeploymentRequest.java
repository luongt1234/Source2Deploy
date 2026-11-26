package com.source2deploy.deploymentservice.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record CreateDeploymentRequest(
        @NotNull(message = "Project ID is required")
        UUID projectId,

        @NotBlank(message = "Repository URL is required")
        String repositoryUrl,

        String branch,
        String commitHash,
        Map<String, String> environmentVariables
) {}
