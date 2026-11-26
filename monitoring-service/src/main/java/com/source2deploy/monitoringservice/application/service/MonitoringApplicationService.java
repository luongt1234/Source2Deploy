package com.source2deploy.monitoringservice.application.service;

import com.source2deploy.monitoringservice.domain.model.DeploymentLog;
import com.source2deploy.monitoringservice.domain.repository.DeploymentLogRepository;

import java.util.List;
import java.util.UUID;

public class MonitoringApplicationService {

    private final DeploymentLogRepository repository;

    public MonitoringApplicationService(DeploymentLogRepository repository) {
        this.repository = repository;
    }

    public DeploymentLog recordLog(UUID deploymentId, String serviceName, String level, String message) {
        DeploymentLog log = DeploymentLog.create(deploymentId, serviceName, level, message);
        return repository.save(log);
    }

    public List<DeploymentLog> getLogs(UUID deploymentId) {
        return repository.findByDeploymentId(deploymentId);
    }
}
