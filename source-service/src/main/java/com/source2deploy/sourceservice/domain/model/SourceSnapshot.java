package com.source2deploy.sourceservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Pure Java Domain Entity - Source Snapshot aggregate root.
 * Free of JPA, Spring or infrastructure dependencies.
 */
public class SourceSnapshot {

    private final UUID id;
    private final UUID projectId;
    private final String repositoryUrl;
    private final String branch;
    private String commitHash;
    private SourceStatus status;
    private String storagePath;
    private final Instant createdAt;
    private Instant updatedAt;
    private Long version;

    public SourceSnapshot(UUID id, UUID projectId, String repositoryUrl, String branch,
                          String commitHash, SourceStatus status, String storagePath,
                          Instant createdAt, Instant updatedAt, Long version) {
        this.id = Objects.requireNonNull(id, "Source ID must not be null");
        this.projectId = Objects.requireNonNull(projectId, "Project ID must not be null");
        this.repositoryUrl = validateUrl(repositoryUrl);
        this.branch = (branch == null || branch.isBlank()) ? "main" : branch.trim();
        this.commitHash = commitHash;
        this.status = status != null ? status : SourceStatus.PENDING;
        this.storagePath = storagePath;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
        this.version = version != null ? version : 0L;
    }

    public static SourceSnapshot create(UUID projectId, String repositoryUrl, String branch, String commitHash) {
        Instant now = Instant.now();
        return new SourceSnapshot(
                UUID.randomUUID(),
                projectId,
                repositoryUrl,
                branch,
                commitHash,
                SourceStatus.PENDING,
                null,
                now,
                now,
                0L
        );
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

    public void startFetching() {
        this.status = SourceStatus.FETCHING;
        this.updatedAt = Instant.now();
    }

    public void markReady(String commitHash, String storagePath) {
        this.status = SourceStatus.READY;
        if (commitHash != null && !commitHash.isBlank()) {
            this.commitHash = commitHash.trim();
        }
        this.storagePath = storagePath;
        this.updatedAt = Instant.now();
    }

    public void markFailed() {
        this.status = SourceStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getProjectId() { return projectId; }
    public String getRepositoryUrl() { return repositoryUrl; }
    public String getBranch() { return branch; }
    public String getCommitHash() { return commitHash; }
    public SourceStatus getStatus() { return status; }
    public String getStoragePath() { return storagePath; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
}
