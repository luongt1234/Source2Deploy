package com.source2deploy.analyzerservice.application.service;

import com.source2deploy.analyzerservice.application.port.output.AnalysisEventPublisherPort;
import com.source2deploy.analyzerservice.domain.model.AnalysisJob;
import com.source2deploy.analyzerservice.domain.model.AnalysisResult;
import com.source2deploy.analyzerservice.domain.repository.AnalysisJobRepository;

import java.util.UUID;

public class AnalysisApplicationService {

    private final AnalysisJobRepository repository;
    private final AnalysisEventPublisherPort publisher;

    public AnalysisApplicationService(AnalysisJobRepository repository, AnalysisEventPublisherPort publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public AnalysisJob analyze(UUID deploymentId, UUID projectId, String repositoryUrl) {
        AnalysisJob job = AnalysisJob.create(deploymentId, projectId);

        // Core logic: detect runtime, framework, build tool and generate configuration
        // In this clean foundation, default to Java 21 / Spring Boot detection
        AnalysisResult detected = new AnalysisResult(
                "Java",
                "Spring Boot",
                "21",
                "maven",
                8080,
                "Dockerfile"
        );

        job.complete(detected);
        AnalysisJob saved = repository.save(job);
        publisher.publishAnalysisCompleted(saved);

        return saved;
    }

    public AnalysisJob getJobById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Analysis job not found: " + id));
    }
}
