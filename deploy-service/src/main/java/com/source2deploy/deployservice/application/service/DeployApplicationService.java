package com.source2deploy.deployservice.application.service;

import com.source2deploy.deployservice.application.port.output.DeployEventPublisherPort;
import com.source2deploy.deployservice.domain.model.ApplicationRelease;
import com.source2deploy.deployservice.domain.repository.ApplicationReleaseRepository;

import java.util.UUID;

public class DeployApplicationService {

    private final ApplicationReleaseRepository repository;
    private final DeployEventPublisherPort publisher;

    public DeployApplicationService(ApplicationReleaseRepository repository, DeployEventPublisherPort publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public ApplicationRelease deploy(UUID deploymentId, UUID projectId, String imageUrl,
                                    String clusterArn, String targetGroupArn, String albDns) {
        ApplicationRelease release = ApplicationRelease.create(
                deploymentId,
                projectId,
                imageUrl,
                clusterArn,
                targetGroupArn
        );

        String liveUrl = "https://" + (albDns != null ? albDns : "app.source2deploy.io");
        release.activate(liveUrl);

        ApplicationRelease saved = repository.save(release);
        publisher.publishDeploymentCompleted(saved);
        return saved;
    }

    public ApplicationRelease getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application release not found: " + id));
    }
}
