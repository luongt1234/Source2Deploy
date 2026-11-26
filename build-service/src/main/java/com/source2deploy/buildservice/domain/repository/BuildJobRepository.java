package com.source2deploy.buildservice.domain.repository;

import com.source2deploy.buildservice.domain.model.BuildJob;

import java.util.Optional;
import java.util.UUID;

public interface BuildJobRepository {
    BuildJob save(BuildJob job);
    Optional<BuildJob> findById(UUID id);
    Optional<BuildJob> findByDeploymentId(UUID deploymentId);
}
