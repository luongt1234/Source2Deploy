package com.source2deploy.registryservice.domain.repository;

import com.source2deploy.registryservice.domain.model.ContainerImage;

import java.util.Optional;
import java.util.UUID;

public interface ContainerImageRepository {
    ContainerImage save(ContainerImage image);
    Optional<ContainerImage> findById(UUID id);
    Optional<ContainerImage> findByDeploymentId(UUID deploymentId);
}
