package com.source2deploy.infrastructureservice;

import com.source2deploy.infrastructureservice.domain.model.InfrastructureDeployment;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InfrastructureTest {

    @Test
    void shouldCreateAndSetInfrastructureReady() {
        UUID deploymentId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        InfrastructureDeployment infra = InfrastructureDeployment.create(deploymentId, projectId, "production");
        assertEquals("PROVISIONING", infra.getStatus());

        infra.ready("arn:aws:ecs:cluster/1", "arn:aws:tg/1", "alb.example.com");
        assertEquals("READY", infra.getStatus());
        assertEquals("alb.example.com", infra.getAlbDnsName());
    }
}
