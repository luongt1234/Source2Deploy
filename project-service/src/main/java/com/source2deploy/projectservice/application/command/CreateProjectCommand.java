package com.source2deploy.projectservice.application.command;

import java.util.UUID;

public record CreateProjectCommand(
        String name,
        String repositoryUrl,
        String defaultBranch,
        UUID githubConnectionId
) {}
