package com.source2deploy.monitoringservice;

import com.source2deploy.monitoringservice.domain.model.DeploymentLog;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MonitoringTest {

    @Test
    void shouldCreateDeploymentLog() {
        UUID deploymentId = UUID.randomUUID();
        DeploymentLog log = DeploymentLog.create(deploymentId, "BuildService", "INFO", "Docker build initiated");

        assertNotNull(log.getId());
        assertEquals(deploymentId, log.getDeploymentId());
        assertEquals("BuildService", log.getSourceService());
        assertEquals("INFO", log.getLogLevel());
        assertEquals("Docker build initiated", log.getMessage());
    }
}
