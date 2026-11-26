package com.source2deploy.deployservice.application.port.output;

import com.source2deploy.deployservice.domain.model.ApplicationRelease;

public interface DeployEventPublisherPort {
    void publishDeploymentCompleted(ApplicationRelease release);
    void publishDeploymentFailed(ApplicationRelease release, String reason);
}
