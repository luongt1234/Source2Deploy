package com.source2deploy.sourceservice.presentation.response;

import com.source2deploy.sourceservice.application.dto.SourceDto;
import com.source2deploy.sourceservice.domain.model.SourceStatus;

import java.time.Instant;
import java.util.UUID;

public record SourceResponse(
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
    public static SourceResponse fromDto(SourceDto dto) {
        return new SourceResponse(
                dto.id(),
                dto.projectId(),
                dto.repositoryUrl(),
                dto.branch(),
                dto.commitHash(),
                dto.status(),
                dto.storagePath(),
                dto.createdAt(),
                dto.updatedAt()
        );
    }
}
