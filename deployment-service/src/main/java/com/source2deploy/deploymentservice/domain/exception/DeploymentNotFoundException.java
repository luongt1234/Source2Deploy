package com.source2deploy.deploymentservice.domain.exception;

import java.util.UUID;

public class DeploymentNotFoundException extends RuntimeException {
    public DeploymentNotFoundException(UUID id) {
        super("Deployment not found with id: " + id);
    }
}
