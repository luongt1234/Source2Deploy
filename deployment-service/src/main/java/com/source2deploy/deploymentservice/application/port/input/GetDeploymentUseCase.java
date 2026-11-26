package com.source2deploy.deploymentservice.application.port.input;

import com.source2deploy.deploymentservice.application.dto.DeploymentDto;

import java.util.List;
import java.util.UUID;

public interface GetDeploymentUseCase {
    DeploymentDto getDeploymentById(UUID id);
    List<DeploymentDto> getDeploymentsByProjectId(UUID projectId);
}
