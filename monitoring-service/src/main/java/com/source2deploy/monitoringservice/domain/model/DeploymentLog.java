package com.source2deploy.monitoringservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class DeploymentLog {

    private final UUID id;
    private final UUID deploymentId;
    private final String sourceService;
    private final String logLevel;
    private final String message;
    private final Instant createdAt;

    public DeploymentLog(UUID id, UUID deploymentId, String sourceService,
                         String logLevel, String message, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "Log ID must not be null");
        this.deploymentId = Objects.requireNonNull(deploymentId, "Deployment ID must not be null");
        this.sourceService = Objects.requireNonNull(sourceService, "Source service must not be null");
        this.logLevel = logLevel != null ? logLevel : "INFO";
        this.message = Objects.requireNonNull(message, "Message must not be null");
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static DeploymentLog create(UUID deploymentId, String sourceService, String level, String message) {
        return new DeploymentLog(UUID.randomUUID(), deploymentId, sourceService, level, message, Instant.now());
    }

    public UUID getId() { return id; }
    public UUID getDeploymentId() { return deploymentId; }
    public String getSourceService() { return sourceService; }
    public String getLogLevel() { return logLevel; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
}
