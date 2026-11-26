package com.source2deploy.analyzerservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "analysis_jobs", indexes = {
        @Index(name = "idx_analysis_jobs_deployment_id", columnList = "deployment_id")
})
@Getter
@Setter
@NoArgsConstructor
public class AnalysisJobJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "deployment_id", nullable = false)
    private UUID deploymentId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "detected_language", length = 100)
    private String detectedLanguage;

    @Column(name = "detected_framework", length = 100)
    private String detectedFramework;

    @Column(name = "runtime_version", length = 50)
    private String runtimeVersion;

    @Column(name = "build_tool", length = 50)
    private String buildTool;

    @Column(name = "port")
    private Integer port;

    @Column(name = "dockerfile_path", length = 255)
    private String dockerfilePath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public AnalysisJobJpaEntity(UUID id, UUID deploymentId, UUID projectId, String status,
                               String detectedLanguage, String detectedFramework, String runtimeVersion,
                               String buildTool, Integer port, String dockerfilePath,
                               Instant createdAt, Instant updatedAt, Long version) {
        this.id = id;
        this.deploymentId = deploymentId;
        this.projectId = projectId;
        this.status = status;
        this.detectedLanguage = detectedLanguage;
        this.detectedFramework = detectedFramework;
        this.runtimeVersion = runtimeVersion;
        this.buildTool = buildTool;
        this.port = port;
        this.dockerfilePath = dockerfilePath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }
}
