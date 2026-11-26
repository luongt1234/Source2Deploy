package com.source2deploy.deploymentservice.application.dto;

import com.source2deploy.deploymentservice.domain.model.DeploymentStage;

import java.time.Instant;
import java.util.UUID;

public record DeploymentStageDto(
        UUID id,
        String stageName,
        String status,
        Instant startedAt,
        Instant completedAt,
        String message
) {
    public static DeploymentStageDto fromDomain(DeploymentStage stage) {
        return new DeploymentStageDto(
                stage.id(),
                stage.stageName(),
                stage.status(),
                stage.startedAt(),
                stage.completedAt(),
                stage.message()
        );
    }
}
