package com.source2deploy.projectservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Pure Java Domain Entity - GitHub Connection
 */
public class GitHubConnection {

    private final UUID id;
    private final String githubUsername;
    private final String accessToken; // masked in presentation
    private final Instant connectedAt;

    public GitHubConnection(UUID id, String githubUsername, String accessToken, Instant connectedAt) {
        this.id = Objects.requireNonNull(id, "ID must not be null");
        this.githubUsername = Objects.requireNonNull(githubUsername, "Username must not be null");
        this.accessToken = Objects.requireNonNull(accessToken, "Access token must not be null");
        this.connectedAt = connectedAt != null ? connectedAt : Instant.now();
    }

    public UUID getId() { return id; }
    public String getGithubUsername() { return githubUsername; }
    public String getAccessToken() { return accessToken; }
    public Instant getConnectedAt() { return connectedAt; }
}
