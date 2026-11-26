package com.source2deploy.deployservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ApplicationRelease {

    private final UUID id;
    private final UUID deploymentId;
    private final UUID projectId;
    private final String imageUrl;
    private final String ecsClusterArn;
    private final String targetGroupArn;
    private ReleaseStatus releaseStatus;
    private String liveUrl;
    private final Instant createdAt;
    private Instant updatedAt;
    private Long version;

    public ApplicationRelease(UUID id, UUID deploymentId, UUID projectId, String imageUrl,
                              String ecsClusterArn, String targetGroupArn, ReleaseStatus releaseStatus,
                              String liveUrl, Instant createdAt, Instant updatedAt, Long version) {
        this.id = Objects.requireNonNull(id, "ID must not be null");
        this.deploymentId = Objects.requireNonNull(deploymentId, "Deployment ID must not be null");
        this.projectId = Objects.requireNonNull(projectId, "Project ID must not be null");
        this.imageUrl = Objects.requireNonNull(imageUrl, "Image URL must not be null");
        this.ecsClusterArn = ecsClusterArn;
        this.targetGroupArn = targetGroupArn;
        this.releaseStatus = releaseStatus != null ? releaseStatus : ReleaseStatus.PENDING;
        this.liveUrl = liveUrl;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
        this.version = version != null ? version : 0L;
    }

    public static ApplicationRelease create(UUID deploymentId, UUID projectId, String imageUrl,
                                           String clusterArn, String tgArn) {
        Instant now = Instant.now();
        return new ApplicationRelease(
                UUID.randomUUID(),
                deploymentId,
                projectId,
                imageUrl,
                clusterArn,
                tgArn,
                ReleaseStatus.DEPLOYING,
                null,
                now,
                now,
                0L
        );
    }

    public void activate(String liveUrl) {
        this.releaseStatus = ReleaseStatus.ACTIVE;
        this.liveUrl = liveUrl;
        this.updatedAt = Instant.now();
    }

    public void fail() {
        this.releaseStatus = ReleaseStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getDeploymentId() { return deploymentId; }
    public UUID getProjectId() { return projectId; }
    public String getImageUrl() { return imageUrl; }
    public String getEcsClusterArn() { return ecsClusterArn; }
    public String getTargetGroupArn() { return targetGroupArn; }
    public ReleaseStatus getReleaseStatus() { return releaseStatus; }
    public String getLiveUrl() { return liveUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
}
