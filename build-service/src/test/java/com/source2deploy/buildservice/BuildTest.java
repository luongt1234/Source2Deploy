package com.source2deploy.buildservice;

import com.source2deploy.buildservice.domain.model.BuildJob;
import com.source2deploy.buildservice.domain.model.BuildStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BuildTest {

    @Test
    void shouldCreateAndSucceedBuildJob() {
        UUID deploymentId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        BuildJob job = BuildJob.create(deploymentId, projectId);
        assertEquals(BuildStatus.QUEUED, job.getStatus());

        job.startBuilding();
        assertEquals(BuildStatus.BUILDING, job.getStatus());

        job.succeed("source2deploy/repo:v1.0.0", 5000L, "Build succeeded");
        assertEquals(BuildStatus.SUCCESS, job.getStatus());
        assertEquals("source2deploy/repo:v1.0.0", job.getImageTag());
    }
}
