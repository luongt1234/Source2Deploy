package com.source2deploy.deploymentservice.application.port.input;

import com.source2deploy.deploymentservice.application.dto.DeploymentDto;

import java.util.UUID;

public interface CancelDeploymentUseCase {
    DeploymentDto cancelDeployment(UUID id);
}
