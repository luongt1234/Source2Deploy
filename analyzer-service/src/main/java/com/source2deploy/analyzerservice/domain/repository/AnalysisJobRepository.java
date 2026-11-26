package com.source2deploy.analyzerservice.domain.repository;

import com.source2deploy.analyzerservice.domain.model.AnalysisJob;

import java.util.Optional;
import java.util.UUID;

public interface AnalysisJobRepository {
    AnalysisJob save(AnalysisJob job);
    Optional<AnalysisJob> findById(UUID id);
    Optional<AnalysisJob> findByDeploymentId(UUID deploymentId);
}
