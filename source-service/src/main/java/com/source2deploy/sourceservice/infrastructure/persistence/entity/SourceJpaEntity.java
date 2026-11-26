package com.source2deploy.sourceservice.infrastructure.persistence.entity;

import com.source2deploy.sourceservice.domain.model.SourceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sources", indexes = {
        @Index(name = "idx_sources_project_id", columnList = "project_id"),
        @Index(name = "idx_sources_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class SourceJpaEntity {

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
    private SourceStatus status;

    @Column(name = "storage_path", length = 512)
    private String storagePath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public SourceJpaEntity(UUID id, UUID projectId, String repositoryUrl, String branch,
                           String commitHash, SourceStatus status, String storagePath,
                           Instant createdAt, Instant updatedAt, Long version) {
        this.id = id;
        this.projectId = projectId;
        this.repositoryUrl = repositoryUrl;
        this.branch = branch;
        this.commitHash = commitHash;
        this.status = status;
        this.storagePath = storagePath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }
}
