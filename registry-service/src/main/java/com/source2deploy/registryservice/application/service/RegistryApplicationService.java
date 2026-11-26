package com.source2deploy.registryservice.application.service;

import com.source2deploy.registryservice.application.port.output.RegistryEventPublisherPort;
import com.source2deploy.registryservice.domain.model.ContainerImage;
import com.source2deploy.registryservice.domain.repository.ContainerImageRepository;

import java.util.UUID;

public class RegistryApplicationService {

    private final ContainerImageRepository repository;
    private final RegistryEventPublisherPort publisher;

    public RegistryApplicationService(ContainerImageRepository repository, RegistryEventPublisherPort publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public ContainerImage pushImage(UUID deploymentId, UUID projectId, String repoName, String tag) {
        String mockDigest = "sha256:" + UUID.randomUUID().toString().replace("-", "");
        String fullUrl = "123456789012.dkr.ecr.us-east-1.amazonaws.com/" + repoName + ":" + tag;

        ContainerImage image = ContainerImage.create(
                deploymentId,
                projectId,
                repoName,
                tag,
                mockDigest,
                fullUrl,
                145000000L
        );

        ContainerImage saved = repository.save(image);
        publisher.publishImagePushed(saved);
        return saved;
    }

    public ContainerImage getImageById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Container image not found: " + id));
    }
}
