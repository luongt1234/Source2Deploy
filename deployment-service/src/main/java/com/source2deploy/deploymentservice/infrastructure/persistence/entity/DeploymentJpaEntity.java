package com.source2deploy.deploymentservice.infrastructure.persistence.entity;

import com.source2deploy.deploymentservice.domain.model.DeploymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "deployments", indexes = {
        @Index(name = "idx_deployments_project_id", columnList = "project_id"),
        @Index(name = "idx_deployments_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class DeploymentJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "repository_url", nullable = false, length = 512)
    private String repositoryUrl;

    @Column(name = "branch", nullable = false, length = 100)
    private String branch;

    @Column(name = "commit_hash", length = 64)
    private String commitHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private DeploymentStatus status;

    @Column(name = "live_url", length = 512)
    private String liveUrl;

    @OneToMany(mappedBy = "deployment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DeploymentStageJpaEntity> stages = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public DeploymentJpaEntity(UUID id, UUID projectId, String repositoryUrl, String branch,
                               String commitHash, DeploymentStatus status, String liveUrl,
                               Instant createdAt, Instant updatedAt, Long version) {
        this.id = id;
        this.projectId = projectId;
        this.repositoryUrl = repositoryUrl;
        this.branch = branch;
        this.commitHash = commitHash;
        this.status = status;
        this.liveUrl = liveUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }
}
