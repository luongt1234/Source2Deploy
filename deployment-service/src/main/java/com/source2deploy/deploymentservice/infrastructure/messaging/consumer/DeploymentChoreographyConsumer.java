package com.source2deploy.deploymentservice.infrastructure.messaging.consumer;

import com.source2deploy.deploymentservice.domain.model.Deployment;
import com.source2deploy.deploymentservice.domain.repository.DeploymentRepository;
import com.source2deploy.event.DomainEvent;
import com.source2deploy.event.KafkaTopics;
import com.source2deploy.event.analysis.AnalysisCompletedEvent;
import com.source2deploy.event.analysis.AnalysisFailedEvent;
import com.source2deploy.event.build.BuildCompletedEvent;
import com.source2deploy.event.build.BuildFailedEvent;
import com.source2deploy.event.build.BuildRequestedEvent;
import com.source2deploy.event.infrastructure.InfrastructureFailedEvent;
import com.source2deploy.event.infrastructure.InfrastructureReadyEvent;
import com.source2deploy.event.infrastructure.InfrastructureRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Event-Driven Choreography Consumer.
 * Orchestrates deployment workflow transitions when events arrive from downstream services.
 */
@Component
public class DeploymentChoreographyConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeploymentChoreographyConsumer.class);

    private final DeploymentRepository deploymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DeploymentChoreographyConsumer(DeploymentRepository deploymentRepository,
                                         KafkaTemplate<String, Object> kafkaTemplate) {
        this.deploymentRepository = deploymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = KafkaTopics.ANALYSIS_EVENTS, groupId = "deployment-service-group")
    public void handleAnalysisEvents(DomainEvent<?> event) {
        log.info("Received analysis event: {} for aggregate: {}", event.eventType(), event.aggregateId());

        if ("AnalysisCompletedEvent".equals(event.eventType())) {
            deploymentRepository.findById(event.aggregateId()).ifPresent(deployment -> {
                deployment.advanceToBuild();
                deploymentRepository.save(deployment);

                // Publish BuildRequestedEvent to build.events
                BuildRequestedEvent buildRequest = new BuildRequestedEvent(
                        UUID.randomUUID(),
                        deployment.getId(),
                        deployment.getProjectId(),
                        deployment.getRepositoryUrl(),
                        deployment.getBranch(),
                        deployment.getCommitHash(),
                        "Dockerfile",
                        "maven",
                        Instant.now()
                );
                DomainEvent<BuildRequestedEvent> buildEvent = DomainEvent.of(
                        "BuildRequestedEvent",
                        deployment.getId(),
                        event.correlationId(),
                        buildRequest
                );
                kafkaTemplate.send(KafkaTopics.BUILD_EVENTS, deployment.getId().toString(), buildEvent);
                log.info("Dispatched BuildRequestedEvent for deployment: {}", deployment.getId());
            });
        }
    }

    @KafkaListener(topics = KafkaTopics.BUILD_EVENTS, groupId = "deployment-service-group")
    public void handleBuildEvents(DomainEvent<?> event) {
        log.info("Received build event: {} for aggregate: {}", event.eventType(), event.aggregateId());

        if ("BuildCompletedEvent".equals(event.eventType())) {
            deploymentRepository.findById(event.aggregateId()).ifPresent(deployment -> {
                deployment.advanceToInfrastructure();
                deploymentRepository.save(deployment);

                // Trigger Infrastructure Provisioning
                InfrastructureRequestedEvent infraRequest = new InfrastructureRequestedEvent(
                        deployment.getId(),
                        deployment.getProjectId(),
                        "production",
                        8080,
                        Instant.now()
                );
                DomainEvent<InfrastructureRequestedEvent> infraEvent = DomainEvent.of(
                        "InfrastructureRequestedEvent",
                        deployment.getId(),
                        event.correlationId(),
                        infraRequest
                );
                kafkaTemplate.send(KafkaTopics.INFRASTRUCTURE_EVENTS, deployment.getId().toString(), infraEvent);
                log.info("Dispatched InfrastructureRequestedEvent for deployment: {}", deployment.getId());
            });
        }
    }
}
