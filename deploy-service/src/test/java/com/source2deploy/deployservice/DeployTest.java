package com.source2deploy.deployservice;

import com.source2deploy.deployservice.domain.model.ApplicationRelease;
import com.source2deploy.deployservice.domain.model.ReleaseStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeployTest {

    @Test
    void shouldCreateAndActivateRelease() {
        UUID deploymentId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        ApplicationRelease release = ApplicationRelease.create(
                deploymentId,
                projectId,
                "123.ecr.aws/app:v1",
                "arn:aws:ecs:cluster/1",
                "arn:aws:tg/1"
        );

        assertEquals(ReleaseStatus.DEPLOYING, release.getReleaseStatus());

        release.activate("https://app.source2deploy.io");
        assertEquals(ReleaseStatus.ACTIVE, release.getReleaseStatus());
        assertEquals("https://app.source2deploy.io", release.getLiveUrl());
    }
}
