package com.source2deploy.registryservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "container_images", indexes = {
        @Index(name = "idx_container_images_deployment_id", columnList = "deployment_id"),
        @Index(name = "idx_container_images_tag", columnList = "image_tag")
})
@Getter
@Setter
@NoArgsConstructor
public class ContainerImageJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "deployment_id", nullable = false)
    private UUID deploymentId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "repository_name", nullable = false)
    private String repositoryName;

    @Column(name = "image_tag", nullable = false, length = 100)
    private String imageTag;

    @Column(name = "image_digest", length = 128)
    private String imageDigest;

    @Column(name = "full_image_url", nullable = false, length = 512)
    private String fullImageUrl;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public ContainerImageJpaEntity(UUID id, UUID deploymentId, UUID projectId, String repositoryName,
                                  String imageTag, String imageDigest, String fullImageUrl,
                                  Long sizeBytes, Instant createdAt, Long version) {
        this.id = id;
        this.deploymentId = deploymentId;
        this.projectId = projectId;
        this.repositoryName = repositoryName;
        this.imageTag = imageTag;
        this.imageDigest = imageDigest;
        this.fullImageUrl = fullImageUrl;
        this.sizeBytes = sizeBytes;
        this.createdAt = createdAt;
        this.version = version;
    }
}
