package com.source2deploy.deploymentservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "deployment_stages", indexes = {
        @Index(name = "idx_deployment_stages_deployment_id", columnList = "deployment_id")
})
@Getter
@Setter
@NoArgsConstructor
public class DeploymentStageJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deployment_id", nullable = false)
    private DeploymentJpaEntity deployment;

    @Column(name = "stage_name", nullable = false, length = 50)
    private String stageName;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    public DeploymentStageJpaEntity(UUID id, DeploymentJpaEntity deployment, String stageName,
                                   String status, Instant startedAt, Instant completedAt, String message) {
        this.id = id;
        this.deployment = deployment;
        this.stageName = stageName;
        this.status = status;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.message = message;
    }
}
