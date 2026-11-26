package com.source2deploy.infrastructureservice.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class InfrastructureDeployment {

    private final UUID id;
    private final UUID deploymentId;
    private final UUID projectId;
    private final String environment;
    private String status;
    private String ecsClusterArn;
    private String targetGroupArn;
    private String albDnsName;
    private final Instant createdAt;
    private Instant updatedAt;
    private Long version;

    public InfrastructureDeployment(UUID id, UUID deploymentId, UUID projectId, String environment,
                                    String status, String ecsClusterArn, String targetGroupArn,
                                    String albDnsName, Instant createdAt, Instant updatedAt, Long version) {
        this.id = Objects.requireNonNull(id, "ID must not be null");
        this.deploymentId = Objects.requireNonNull(deploymentId, "Deployment ID must not be null");
        this.projectId = Objects.requireNonNull(projectId, "Project ID must not be null");
        this.environment = environment != null ? environment : "production";
        this.status = status != null ? status : "PROVISIONING";
        this.ecsClusterArn = ecsClusterArn;
        this.targetGroupArn = targetGroupArn;
        this.albDnsName = albDnsName;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
        this.version = version != null ? version : 0L;
    }

    public static InfrastructureDeployment create(UUID deploymentId, UUID projectId, String env) {
        Instant now = Instant.now();
        return new InfrastructureDeployment(
                UUID.randomUUID(),
                deploymentId,
                projectId,
                env,
                "PROVISIONING",
                null,
                null,
                null,
                now,
                now,
                0L
        );
    }

    public void ready(String clusterArn, String tgArn, String albDns) {
        this.status = "READY";
        this.ecsClusterArn = clusterArn;
        this.targetGroupArn = tgArn;
        this.albDnsName = albDns;
        this.updatedAt = Instant.now();
    }

    public void fail(String reason) {
        this.status = "FAILED";
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getDeploymentId() { return deploymentId; }
    public UUID getProjectId() { return projectId; }
    public String getEnvironment() { return environment; }
    public String getStatus() { return status; }
    public String getEcsClusterArn() { return ecsClusterArn; }
    public String getTargetGroupArn() { return targetGroupArn; }
    public String getAlbDnsName() { return albDnsName; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
}
