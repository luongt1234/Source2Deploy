package com.source2deploy.sourceservice.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record FetchSourceRequest(
        @NotNull(message = "Project ID is required")
        UUID projectId,

        @NotBlank(message = "Repository URL is required")
        @Pattern(regexp = "^(https://github\\.com/[^/]+/[^/]+|git@github\\.com:[^/]+/[^/]+)(\\.git)?$",
                message = "Invalid GitHub repository URL format")
        String repositoryUrl,

        String branch,
        String commitHash
) {}
