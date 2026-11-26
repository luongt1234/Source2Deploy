package com.source2deploy.projectservice.infrastructure.persistence.entity;

import com.source2deploy.projectservice.domain.model.ProjectStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "projects", indexes = {
        @Index(name = "idx_projects_repository_url", columnList = "repository_url"),
        @Index(name = "idx_projects_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class ProjectJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "repository_url", nullable = false, length = 512)
    private String repositoryUrl;

    @Column(name = "default_branch", nullable = false, length = 100)
    private String defaultBranch;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ProjectStatus status;

    @Column(name = "github_connection_id")
    private UUID githubConnectionId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public ProjectJpaEntity(UUID id, String name, String repositoryUrl, String defaultBranch,
                            ProjectStatus status, UUID githubConnectionId, Instant createdAt,
                            Instant updatedAt, Long version) {
        this.id = id;
        this.name = name;
        this.repositoryUrl = repositoryUrl;
        this.defaultBranch = defaultBranch;
        this.status = status;
        this.githubConnectionId = githubConnectionId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }
}
