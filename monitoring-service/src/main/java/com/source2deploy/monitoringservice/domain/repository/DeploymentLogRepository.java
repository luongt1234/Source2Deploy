package com.source2deploy.monitoringservice.domain.repository;

import com.source2deploy.monitoringservice.domain.model.DeploymentLog;

import java.util.List;
import java.util.UUID;

public interface DeploymentLogRepository {
    DeploymentLog save(DeploymentLog log);
    List<DeploymentLog> findByDeploymentId(UUID deploymentId);
}
