package com.source2deploy.projectservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Pure Java Domain Entity - Project aggregate root.
 * Free of JPA, Spring or infrastructure dependencies.
 */
public class Project {

    private final UUID id;
    private String name;
    private String repositoryUrl;
    private String defaultBranch;
    private ProjectStatus status;
    private UUID githubConnectionId;
    private final Instant createdAt;
    private Instant updatedAt;
    private Long version;

    public Project(UUID id, String name, String repositoryUrl, String defaultBranch,
                   ProjectStatus status, UUID githubConnectionId, Instant createdAt,
                   Instant updatedAt, Long version) {
        this.id = Objects.requireNonNull(id, "Project ID must not be null");
        this.name = validateName(name);
        this.repositoryUrl = validateUrl(repositoryUrl);
        this.defaultBranch = (defaultBranch == null || defaultBranch.isBlank()) ? "main" : defaultBranch;
        this.status = status != null ? status : ProjectStatus.ACTIVE;
        this.githubConnectionId = githubConnectionId;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
        this.version = version != null ? version : 0L;
    }

    public static Project create(String name, String repositoryUrl, String defaultBranch, UUID githubConnectionId) {
        Instant now = Instant.now();
        return new Project(
                UUID.randomUUID(),
                name,
                repositoryUrl,
                defaultBranch,
                ProjectStatus.ACTIVE,
                githubConnectionId,
                now,
                now,
                0L
        );
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
        return name.trim();
    }

    private String validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Repository URL cannot be empty");
        }
        if (!url.startsWith("https://github.com/") && !url.startsWith("git@github.com:")) {
            throw new IllegalArgumentException("Invalid GitHub repository URL: " + url);
        }
        return url.trim();
    }

    public void updateDetails(String newName, String newDefaultBranch) {
        this.name = validateName(newName);
        if (newDefaultBranch != null && !newDefaultBranch.isBlank()) {
            this.defaultBranch = newDefaultBranch.trim();
        }
        this.updatedAt = Instant.now();
    }

    public void archive() {
        this.status = ProjectStatus.ARCHIVED;
        this.updatedAt = Instant.now();
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getRepositoryUrl() { return repositoryUrl; }
    public String getDefaultBranch() { return defaultBranch; }
    public ProjectStatus getStatus() { return status; }
    public UUID getGithubConnectionId() { return githubConnectionId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
}
