package com.source2deploy.buildservice.application.service;

import com.source2deploy.buildservice.application.port.output.BuildEventPublisherPort;
import com.source2deploy.buildservice.domain.model.BuildJob;
import com.source2deploy.buildservice.domain.repository.BuildJobRepository;

import java.util.UUID;

public class BuildApplicationService {

    private final BuildJobRepository repository;
    private final BuildEventPublisherPort publisher;

    public BuildApplicationService(BuildJobRepository repository, BuildEventPublisherPort publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public BuildJob triggerBuild(UUID deploymentId, UUID projectId, String repositoryUrl, String commitHash) {
        BuildJob job = BuildJob.create(deploymentId, projectId);
        job.startBuilding();

        // In foundation layer: generate image tag
        String shortCommit = (commitHash != null && commitHash.length() >= 7) ? commitHash.substring(0, 7) : "latest";
        String imageTag = "source2deploy/" + projectId + ":" + shortCommit;

        job.succeed(imageTag, 12500L, "Successfully built image " + imageTag);
        BuildJob saved = repository.save(job);
        publisher.publishBuildCompleted(saved);

        return saved;
    }

    public BuildJob getJobById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Build job not found: " + id));
    }
}
