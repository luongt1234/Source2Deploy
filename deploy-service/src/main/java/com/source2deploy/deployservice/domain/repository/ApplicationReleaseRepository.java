package com.source2deploy.deployservice.domain.repository;

import com.source2deploy.deployservice.domain.model.ApplicationRelease;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationReleaseRepository {
    ApplicationRelease save(ApplicationRelease release);
    Optional<ApplicationRelease> findById(UUID id);
    Optional<ApplicationRelease> findByDeploymentId(UUID deploymentId);
}
