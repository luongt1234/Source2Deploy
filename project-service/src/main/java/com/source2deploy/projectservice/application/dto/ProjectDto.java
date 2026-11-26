package com.source2deploy.projectservice.application.dto;

import com.source2deploy.projectservice.domain.model.Project;
import com.source2deploy.projectservice.domain.model.ProjectStatus;

import java.time.Instant;
import java.util.UUID;

public record ProjectDto(
        UUID id,
        String name,
        String repositoryUrl,
        String defaultBranch,
        ProjectStatus status,
        UUID githubConnectionId,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProjectDto fromDomain(Project project) {
        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getRepositoryUrl(),
                project.getDefaultBranch(),
                project.getStatus(),
                project.getGithubConnectionId(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
