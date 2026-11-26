package com.source2deploy.deploymentservice.domain.repository;

import com.source2deploy.deploymentservice.domain.model.Deployment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeploymentRepository {
    Deployment save(Deployment deployment);
    Optional<Deployment> findById(UUID id);
    List<Deployment> findByProjectId(UUID projectId);
}
