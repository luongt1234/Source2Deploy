package com.source2deploy.deploymentservice.presentation.response;

import com.source2deploy.deploymentservice.application.dto.DeploymentDto;
import com.source2deploy.deploymentservice.domain.model.DeploymentStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DeploymentResponse(
        UUID id,
        UUID projectId,
        String repositoryUrl,
        String branch,
        String commitHash,
        DeploymentStatus status,
        String liveUrl,
        List<DeploymentStageResponse> stages,
        Instant createdAt,
        Instant updatedAt
) {
    public static DeploymentResponse fromDto(DeploymentDto dto) {
        List<DeploymentStageResponse> stages = dto.stages().stream()
                .map(DeploymentStageResponse::fromDto)
                .toList();

        return new DeploymentResponse(
                dto.id(),
                dto.projectId(),
                dto.repositoryUrl(),
                dto.branch(),
                dto.commitHash(),
                dto.status(),
                dto.liveUrl(),
                stages,
                dto.createdAt(),
                dto.updatedAt()
        );
    }
}
