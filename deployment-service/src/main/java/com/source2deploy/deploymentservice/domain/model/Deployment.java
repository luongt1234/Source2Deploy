package com.source2deploy.deploymentservice.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Pure Java Domain Entity - Deployment Aggregate Root.
 */
public class Deployment {

    private final UUID id;
    private final UUID projectId;
    private final String repositoryUrl;
    private final String branch;
    private String commitHash;
    private DeploymentStatus status;
    private String liveUrl;
    private final List<DeploymentStage> stages;
    private final Instant createdAt;
    private Instant updatedAt;
    private Long version;

    public Deployment(UUID id, UUID projectId, String repositoryUrl, String branch,
                      String commitHash, DeploymentStatus status, String liveUrl,
                      List<DeploymentStage> stages, Instant createdAt, Instant updatedAt,
                      Long version) {
        this.id = Objects.requireNonNull(id, "Deployment ID must not be null");
        this.projectId = Objects.requireNonNull(projectId, "Project ID must not be null");
        this.repositoryUrl = Objects.requireNonNull(repositoryUrl, "Repository URL must not be null");
        this.branch = (branch == null || branch.isBlank()) ? "main" : branch;
        this.commitHash = commitHash;
        this.status = status != null ? status : DeploymentStatus.PENDING;
        this.liveUrl = liveUrl;
        this.stages = stages != null ? new ArrayList<>(stages) : new ArrayList<>();
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
        this.version = version != null ? version : 0L;
    }

    public static Deployment create(UUID projectId, String repositoryUrl, String branch, String commitHash) {
        Instant now = Instant.now();
        return new Deployment(
                UUID.randomUUID(),
                projectId,
                repositoryUrl,
                branch,
                commitHash,
                DeploymentStatus.PENDING,
                null,
                new ArrayList<>(),
                now,
                now,
                0L
        );
    }

    public void startAnalysis() {
        this.status = DeploymentStatus.ANALYZING;
        this.stages.add(DeploymentStage.create("SOURCE_ANALYSIS"));
        this.updatedAt = Instant.now();
    }

    public void advanceToBuild() {
        this.status = DeploymentStatus.BUILDING;
        this.stages.add(DeploymentStage.create("CONTAINER_BUILD"));
        this.updatedAt = Instant.now();
    }

    public void advanceToInfrastructure() {
        this.status = DeploymentStatus.PROVISIONING_INFRA;
        this.stages.add(DeploymentStage.create("INFRASTRUCTURE_PROVISIONING"));
        this.updatedAt = Instant.now();
    }

    public void advanceToDeploy() {
        this.status = DeploymentStatus.DEPLOYING;
        this.stages.add(DeploymentStage.create("APPLICATION_DEPLOYMENT"));
        this.updatedAt = Instant.now();
    }

    public void complete(String liveUrl) {
        this.status = DeploymentStatus.COMPLETED;
        this.liveUrl = liveUrl;
        this.updatedAt = Instant.now();
    }

    public void fail(String stage, String reason) {
        this.status = DeploymentStatus.FAILED;
        this.stages.add(new DeploymentStage(UUID.randomUUID(), stage, "FAILED", Instant.now(), Instant.now(), reason));
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (this.status == DeploymentStatus.COMPLETED || this.status == DeploymentStatus.FAILED) {
            throw new IllegalStateException("Cannot cancel a completed or failed deployment");
        }
        this.status = DeploymentStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getProjectId() { return projectId; }
    public String getRepositoryUrl() { return repositoryUrl; }
    public String getBranch() { return branch; }
    public String getCommitHash() { return commitHash; }
    public DeploymentStatus getStatus() { return status; }
    public String getLiveUrl() { return liveUrl; }
    public List<DeploymentStage> getStages() { return Collections.unmodifiableList(stages); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
}
