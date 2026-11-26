package com.source2deploy.sourceservice.application.command;

import java.util.UUID;

public record FetchSourceCommand(
        UUID projectId,
        String repositoryUrl,
        String branch,
        String commitHash
) {}
