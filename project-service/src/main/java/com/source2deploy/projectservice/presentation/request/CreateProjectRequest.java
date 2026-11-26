package com.source2deploy.projectservice.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateProjectRequest(
        @NotBlank(message = "Project name is required")
        @Size(min = 2, max = 100, message = "Project name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Repository URL is required")
        @Pattern(regexp = "^(https://github\\.com/[^/]+/[^/]+|git@github\\.com:[^/]+/[^/]+)(\\.git)?$",
                message = "Invalid GitHub repository URL format")
        String repositoryUrl,

        String defaultBranch,
        UUID githubConnectionId
) {}
