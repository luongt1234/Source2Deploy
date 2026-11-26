package com.source2deploy.buildservice.infrastructure.persistence.entity;

import com.source2deploy.buildservice.domain.model.BuildStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "build_jobs", indexes = {
        @Index(name = "idx_build_jobs_deployment_id", columnList = "deployment_id")
})
@Getter
@Setter
@NoArgsConstructor
public class BuildJobJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "deployment_id", nullable = false)
    private UUID deploymentId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private BuildStatus status;

    @Column(name = "image_tag")
    private String imageTag;

    @Column(name = "build_duration_ms")
    private Long buildDurationMs;

    @Column(name = "build_logs", columnDefinition = "TEXT")
    private String buildLogs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public BuildJobJpaEntity(UUID id, UUID deploymentId, UUID projectId, BuildStatus status,
                            String imageTag, Long buildDurationMs, String buildLogs,
                            Instant createdAt, Instant updatedAt, Long version) {
        this.id = id;
        this.deploymentId = deploymentId;
        this.projectId = projectId;
        this.status = status;
        this.imageTag = imageTag;
        this.buildDurationMs = buildDurationMs;
        this.buildLogs = buildLogs;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }
}
