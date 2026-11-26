package com.source2deploy.analyzerservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Pure Java Domain Entity - AnalysisJob aggregate root.
 */
public class AnalysisJob {

    private final UUID id;
    private final UUID deploymentId;
    private final UUID projectId;
    private String status;
    private AnalysisResult result;
    private final Instant createdAt;
    private Instant updatedAt;
    private Long version;

    public AnalysisJob(UUID id, UUID deploymentId, UUID projectId, String status,
                       AnalysisResult result, Instant createdAt, Instant updatedAt, Long version) {
        this.id = Objects.requireNonNull(id, "Job ID must not be null");
        this.deploymentId = Objects.requireNonNull(deploymentId, "Deployment ID must not be null");
        this.projectId = Objects.requireNonNull(projectId, "Project ID must not be null");
        this.status = status != null ? status : "PENDING";
        this.result = result;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
        this.version = version != null ? version : 0L;
    }

    public static AnalysisJob create(UUID deploymentId, UUID projectId) {
        Instant now = Instant.now();
        return new AnalysisJob(
                UUID.randomUUID(),
                deploymentId,
                projectId,
                "ANALYZING",
                null,
                now,
                now,
                0L
        );
    }

    public void complete(AnalysisResult result) {
        this.status = "COMPLETED";
        this.result = result;
        this.updatedAt = Instant.now();
    }

    public void fail(String reason) {
        this.status = "FAILED";
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getDeploymentId() { return deploymentId; }
    public UUID getProjectId() { return projectId; }
    public String getStatus() { return status; }
    public AnalysisResult getResult() { return result; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
}
