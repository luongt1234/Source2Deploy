package com.source2deploy.sourceservice.application.dto;

import com.source2deploy.sourceservice.domain.model.SourceSnapshot;
import com.source2deploy.sourceservice.domain.model.SourceStatus;

import java.time.Instant;
import java.util.UUID;

public record SourceDto(
        UUID id,
        UUID projectId,
        String repositoryUrl,
        String branch,
        String commitHash,
        SourceStatus status,
        String storagePath,
        Instant createdAt,
        Instant updatedAt
) {
    public static SourceDto fromDomain(SourceSnapshot snapshot) {
        return new SourceDto(
                snapshot.getId(),
                snapshot.getProjectId(),
                snapshot.getRepositoryUrl(),
                snapshot.getBranch(),
                snapshot.getCommitHash(),
                snapshot.getStatus(),
                snapshot.getStoragePath(),
                snapshot.getCreatedAt(),
                snapshot.getUpdatedAt()
        );
    }
}
