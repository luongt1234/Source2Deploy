package com.source2deploy.monitoringservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "deployment_logs", indexes = {
        @Index(name = "idx_deployment_logs_deployment_id", columnList = "deployment_id")
})
@Getter
@Setter
@NoArgsConstructor
public class DeploymentLogJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "deployment_id", nullable = false)
    private UUID deploymentId;

    @Column(name = "source_service", nullable = false, length = 100)
    private String sourceService;

    @Column(name = "log_level", nullable = false, length = 20)
    private String logLevel;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public DeploymentLogJpaEntity(UUID id, UUID deploymentId, String sourceService,
                                 String logLevel, String message, Instant createdAt) {
        this.id = id;
        this.deploymentId = deploymentId;
        this.sourceService = sourceService;
        this.logLevel = logLevel;
        this.message = message;
        this.createdAt = createdAt;
    }
}
