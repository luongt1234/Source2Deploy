package com.source2deploy.infrastructureservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "infrastructure_deployments", indexes = {
        @Index(name = "idx_infra_deployments_deployment_id", columnList = "deployment_id")
})
@Getter
@Setter
@NoArgsConstructor
public class InfrastructureDeploymentJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "deployment_id", nullable = false)
    private UUID deploymentId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "environment", nullable = false, length = 50)
    private String environment;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "ecs_cluster_arn", length = 512)
    private String ecsClusterArn;

    @Column(name = "target_group_arn", length = 512)
    private String targetGroupArn;

    @Column(name = "alb_dns_name", length = 512)
    private String albDnsName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public InfrastructureDeploymentJpaEntity(UUID id, UUID deploymentId, UUID projectId, String environment,
                                            String status, String ecsClusterArn, String targetGroupArn,
                                            String albDnsName, Instant createdAt, Instant updatedAt, Long version) {
        this.id = id;
        this.deploymentId = deploymentId;
        this.projectId = projectId;
        this.environment = environment;
        this.status = status;
        this.ecsClusterArn = ecsClusterArn;
        this.targetGroupArn = targetGroupArn;
        this.albDnsName = albDnsName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }
}
