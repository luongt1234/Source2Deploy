package com.source2deploy.deploymentservice.application.port.output;

import com.source2deploy.deploymentservice.domain.model.Deployment;

import java.util.Map;

public interface DeploymentEventPublisherPort {
    void publishDeploymentStarted(Deployment deployment, Map<String, String> envVars);
    void publishDeploymentCompleted(Deployment deployment);
    void publishDeploymentFailed(Deployment deployment, String failedStage, String reason);
}
