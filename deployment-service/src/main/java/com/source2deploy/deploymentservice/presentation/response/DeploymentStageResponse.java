package com.source2deploy.deploymentservice.presentation.response;

import com.source2deploy.deploymentservice.application.dto.DeploymentStageDto;

import java.time.Instant;
import java.util.UUID;

public record DeploymentStageResponse(
        UUID id,
        String stageName,
        String status,
        Instant startedAt,
        Instant completedAt,
        String message
) {
    public static DeploymentStageResponse fromDto(DeploymentStageDto dto) {
        return new DeploymentStageResponse(
                dto.id(),
                dto.stageName(),
                dto.status(),
                dto.startedAt(),
                dto.completedAt(),
                dto.message()
        );
    }
}
