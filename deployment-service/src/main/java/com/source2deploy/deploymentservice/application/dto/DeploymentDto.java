package com.source2deploy.deploymentservice.application.dto;

import com.source2deploy.deploymentservice.domain.model.Deployment;
import com.source2deploy.deploymentservice.domain.model.DeploymentStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DeploymentDto(
        UUID id,
        UUID projectId,
        String repositoryUrl,
        String branch,
        String commitHash,
        DeploymentStatus status,
        String liveUrl,
        List<DeploymentStageDto> stages,
        Instant createdAt,
        Instant updatedAt
) {
    public static DeploymentDto fromDomain(Deployment deployment) {
        List<DeploymentStageDto> stages = deployment.getStages().stream()
                .map(DeploymentStageDto::fromDomain)
                .toList();

        return new DeploymentDto(
                deployment.getId(),
                deployment.getProjectId(),
                deployment.getRepositoryUrl(),
                deployment.getBranch(),
                deployment.getCommitHash(),
                deployment.getStatus(),
                deployment.getLiveUrl(),
                stages,
                deployment.getCreatedAt(),
                deployment.getUpdatedAt()
        );
    }
}
