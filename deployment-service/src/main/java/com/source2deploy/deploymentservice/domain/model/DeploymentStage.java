package com.source2deploy.deploymentservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DeploymentStage(
        UUID id,
        String stageName,
        String status,
        Instant startedAt,
        Instant completedAt,
        String message
) {
    public static DeploymentStage create(String stageName) {
        return new DeploymentStage(UUID.randomUUID(), stageName, "IN_PROGRESS", Instant.now(), null, null);
    }

    public DeploymentStage complete(String message) {
        return new DeploymentStage(this.id, this.stageName, "COMPLETED", this.startedAt, Instant.now(), message);
    }

    public DeploymentStage fail(String reason) {
        return new DeploymentStage(this.id, this.stageName, "FAILED", this.startedAt, Instant.now(), reason);
    }
}
