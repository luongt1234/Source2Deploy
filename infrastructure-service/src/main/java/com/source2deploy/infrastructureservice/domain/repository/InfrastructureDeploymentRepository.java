package com.source2deploy.infrastructureservice.domain.repository;

import com.source2deploy.infrastructureservice.domain.model.InfrastructureDeployment;

import java.util.Optional;
import java.util.UUID;

public interface InfrastructureDeploymentRepository {
    InfrastructureDeployment save(InfrastructureDeployment deployment);
    Optional<InfrastructureDeployment> findById(UUID id);
    Optional<InfrastructureDeployment> findByDeploymentId(UUID deploymentId);
}
