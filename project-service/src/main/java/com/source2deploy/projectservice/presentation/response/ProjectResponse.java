package com.source2deploy.projectservice.presentation.response;

import com.source2deploy.projectservice.application.dto.ProjectDto;
import com.source2deploy.projectservice.domain.model.ProjectStatus;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String repositoryUrl,
        String defaultBranch,
        ProjectStatus status,
        UUID githubConnectionId,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectResponse fromDto(ProjectDto dto) {
        return new ProjectResponse(
                dto.id(),
                dto.name(),
                dto.repositoryUrl(),
                dto.defaultBranch(),
                dto.status(),
                dto.githubConnectionId(),
                dto.createdAt(),
                dto.updatedAt()
        );
    }
}
