package com.source2deploy.registryservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ContainerImage {

    private final UUID id;
    private final UUID deploymentId;
    private final UUID projectId;
    private final String repositoryName;
    private final String imageTag;
    private final String imageDigest;
    private final String fullImageUrl;
    private final Long sizeBytes;
    private final Instant createdAt;
    private final Long version;

    public ContainerImage(UUID id, UUID deploymentId, UUID projectId, String repositoryName,
                          String imageTag, String imageDigest, String fullImageUrl,
                          Long sizeBytes, Instant createdAt, Long version) {
        this.id = Objects.requireNonNull(id, "Image ID must not be null");
        this.deploymentId = Objects.requireNonNull(deploymentId, "Deployment ID must not be null");
        this.projectId = Objects.requireNonNull(projectId, "Project ID must not be null");
        this.repositoryName = Objects.requireNonNull(repositoryName, "Repository name must not be null");
        this.imageTag = Objects.requireNonNull(imageTag, "Image tag must not be null");
        this.imageDigest = imageDigest;
        this.fullImageUrl = Objects.requireNonNull(fullImageUrl, "Full image URL must not be null");
        this.sizeBytes = sizeBytes != null ? sizeBytes : 0L;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.version = version != null ? version : 0L;
    }

    public static ContainerImage create(UUID deploymentId, UUID projectId, String repoName, String tag, String digest, String fullUrl, Long size) {
        return new ContainerImage(
                UUID.randomUUID(),
                deploymentId,
                projectId,
                repoName,
                tag,
                digest,
                fullUrl,
                size,
                Instant.now(),
                0L
        );
    }

    public UUID getId() { return id; }
    public UUID getDeploymentId() { return deploymentId; }
    public UUID getProjectId() { return projectId; }
    public String getRepositoryName() { return repositoryName; }
    public String getImageTag() { return imageTag; }
    public String getImageDigest() { return imageDigest; }
    public String getFullImageUrl() { return fullImageUrl; }
    public Long getSizeBytes() { return sizeBytes; }
    public Instant getCreatedAt() { return createdAt; }
    public Long getVersion() { return version; }
}
