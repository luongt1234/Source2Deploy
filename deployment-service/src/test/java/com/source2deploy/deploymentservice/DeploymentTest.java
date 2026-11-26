package com.source2deploy.deploymentservice;

import com.source2deploy.deploymentservice.domain.model.Deployment;
import com.source2deploy.deploymentservice.domain.model.DeploymentStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeploymentTest {

    @Test
    void shouldCreateAndAdvanceDeploymentStages() {
        UUID projectId = UUID.randomUUID();
        Deployment deployment = Deployment.create(
                projectId,
                "https://github.com/source2deploy/sample-app.git",
                "main",
                "abc1234"
        );

        assertEquals(DeploymentStatus.PENDING, deployment.getStatus());
        assertEquals(0, deployment.getStages().size());

        deployment.startAnalysis();
        assertEquals(DeploymentStatus.ANALYZING, deployment.getStatus());
        assertEquals(1, deployment.getStages().size());
        assertEquals("SOURCE_ANALYSIS", deployment.getStages().get(0).stageName());

        deployment.advanceToBuild();
        assertEquals(DeploymentStatus.BUILDING, deployment.getStatus());
        assertEquals(2, deployment.getStages().size());

        deployment.complete("https://sample-app.source2deploy.io");
        assertEquals(DeploymentStatus.COMPLETED, deployment.getStatus());
        assertEquals("https://sample-app.source2deploy.io", deployment.getLiveUrl());
    }

    @Test
    void shouldThrowExceptionWhenCancellingCompletedDeployment() {
        Deployment deployment = Deployment.create(UUID.randomUUID(), "url", "main", "hash");
        deployment.complete("https://live.url");

        assertThrows(IllegalStateException.class, deployment::cancel);
    }
}
