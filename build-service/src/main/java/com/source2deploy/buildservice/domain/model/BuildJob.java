package com.source2deploy.buildservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class BuildJob {

    private final UUID id;
    private final UUID deploymentId;
    private final UUID projectId;
    private BuildStatus status;
    private String imageTag;
    private Long buildDurationMs;
    private String buildLogs;
    private final Instant createdAt;
    private Instant updatedAt;
    private Long version;

    public BuildJob(UUID id, UUID deploymentId, UUID projectId, BuildStatus status,
                    String imageTag, Long buildDurationMs, String buildLogs,
                    Instant createdAt, Instant updatedAt, Long version) {
        this.id = Objects.requireNonNull(id, "BuildJob ID must not be null");
        this.deploymentId = Objects.requireNonNull(deploymentId, "Deployment ID must not be null");
        this.projectId = Objects.requireNonNull(projectId, "Project ID must not be null");
        this.status = status != null ? status : BuildStatus.QUEUED;
        this.imageTag = imageTag;
        this.buildDurationMs = buildDurationMs;
        this.buildLogs = buildLogs;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
        this.version = version != null ? version : 0L;
    }

    public static BuildJob create(UUID deploymentId, UUID projectId) {
        Instant now = Instant.now();
        return new BuildJob(
                UUID.randomUUID(),
                deploymentId,
                projectId,
                BuildStatus.QUEUED,
                null,
                null,
                null,
                now,
                now,
                0L
        );
    }

    public void startBuilding() {
        this.status = BuildStatus.BUILDING;
        this.updatedAt = Instant.now();
    }

    public void succeed(String imageTag, long durationMs, String logs) {
        this.status = BuildStatus.SUCCESS;
        this.imageTag = imageTag;
        this.buildDurationMs = durationMs;
        this.buildLogs = logs;
        this.updatedAt = Instant.now();
    }

    public void fail(String logs) {
        this.status = BuildStatus.FAILED;
        this.buildLogs = logs;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getDeploymentId() { return deploymentId; }
    public UUID getProjectId() { return projectId; }
    public BuildStatus getStatus() { return status; }
    public String getImageTag() { return imageTag; }
    public Long getBuildDurationMs() { return buildDurationMs; }
    public String getBuildLogs() { return buildLogs; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
}
