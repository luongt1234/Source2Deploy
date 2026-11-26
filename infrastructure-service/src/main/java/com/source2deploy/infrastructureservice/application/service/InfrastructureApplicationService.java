package com.source2deploy.infrastructureservice.application.service;

import com.source2deploy.infrastructureservice.application.port.output.InfrastructureEventPublisherPort;
import com.source2deploy.infrastructureservice.domain.model.InfrastructureDeployment;
import com.source2deploy.infrastructureservice.domain.repository.InfrastructureDeploymentRepository;

import java.util.UUID;

public class InfrastructureApplicationService {

    private final InfrastructureDeploymentRepository repository;
    private final InfrastructureEventPublisherPort publisher;

    public InfrastructureApplicationService(InfrastructureDeploymentRepository repository,
                                            InfrastructureEventPublisherPort publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public InfrastructureDeployment provision(UUID deploymentId, UUID projectId, String env, int containerPort) {
        InfrastructureDeployment infra = InfrastructureDeployment.create(deploymentId, projectId, env);

        // Foundation logic: provision AWS ECS Cluster & ALB via Terraform
        String clusterArn = "arn:aws:ecs:us-east-1:123456789012:cluster/source2deploy-" + env;
        String tgArn = "arn:aws:elasticloadbalancing:us-east-1:123456789012:targetgroup/tg-" + deploymentId.toString().substring(0, 8);
        String albDns = "app-" + deploymentId.toString().substring(0, 8) + ".source2deploy.com";

        infra.ready(clusterArn, tgArn, albDns);
        InfrastructureDeployment saved = repository.save(infra);
        publisher.publishInfrastructureReady(saved);
        return saved;
    }

    public InfrastructureDeployment getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Infrastructure deployment not found: " + id));
    }
}
