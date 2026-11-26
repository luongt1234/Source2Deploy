package com.source2deploy.deploymentservice.domain.model;

public enum DeploymentStatus {
    PENDING,
    ANALYZING,
    BUILDING,
    PUSHING_IMAGE,
    PROVISIONING_INFRA,
    DEPLOYING,
    COMPLETED,
    FAILED,
    CANCELLED
}
