package com.source2deploy.registryservice;

import com.source2deploy.registryservice.domain.model.ContainerImage;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RegistryTest {

    @Test
    void shouldCreateContainerImage() {
        UUID deploymentId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        ContainerImage image = ContainerImage.create(
                deploymentId,
                projectId,
                "my-repo",
                "latest",
                "sha256:123456",
                "123456.dkr.ecr.us-east-1.amazonaws.com/my-repo:latest",
                12000000L
        );

        assertNotNull(image.getId());
        assertEquals("my-repo", image.getRepositoryName());
        assertEquals("latest", image.getImageTag());
        assertEquals("sha256:123456", image.getImageDigest());
    }
}
