package com.source2deploy.deployservice.infrastructure.persistence.entity;

import com.source2deploy.deployservice.domain.model.ReleaseStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "application_releases", indexes = {
        @Index(name = "idx_app_releases_deployment_id", columnList = "deployment_id"),
        @Index(name = "idx_app_releases_status", columnList = "release_status")
})
@Getter
@Setter
@NoArgsConstructor
public class ApplicationReleaseJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "deployment_id", nullable = false)
    private UUID deploymentId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "image_url", nullable = false, length = 512)
    private String imageUrl;

    @Column(name = "ecs_cluster_arn", length = 512)
    private String ecsClusterArn;

    @Column(name = "target_group_arn", length = 512)
    private String targetGroupArn;

    @Enumerated(EnumType.STRING)
    @Column(name = "release_status", nullable = false, length = 50)
    private ReleaseStatus releaseStatus;

    @Column(name = "live_url", length = 512)
    private String liveUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public ApplicationReleaseJpaEntity(UUID id, UUID deploymentId, UUID projectId, String imageUrl,
                                      String ecsClusterArn, String targetGroupArn, ReleaseStatus releaseStatus,
                                      String liveUrl, Instant createdAt, Instant updatedAt, Long version) {
        this.id = id;
        this.deploymentId = deploymentId;
        this.projectId = projectId;
        this.imageUrl = imageUrl;
        this.ecsClusterArn = ecsClusterArn;
        this.targetGroupArn = targetGroupArn;
        this.releaseStatus = releaseStatus;
        this.liveUrl = liveUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }
}
