package com.source2deploy.buildservice.infrastructure.messaging.producer;

import com.source2deploy.buildservice.application.port.output.BuildEventPublisherPort;
import com.source2deploy.buildservice.domain.model.BuildJob;
import com.source2deploy.event.DomainEvent;
import com.source2deploy.event.KafkaTopics;
import com.source2deploy.event.build.BuildCompletedEvent;
import com.source2deploy.event.build.BuildFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class KafkaBuildEventPublisher implements BuildEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaBuildEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaBuildEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishBuildCompleted(BuildJob job) {
        BuildCompletedEvent payload = new BuildCompletedEvent(
                job.getId(),
                job.getDeploymentId(),
                job.getProjectId(),
                job.getImageTag(),
                job.getBuildDurationMs() != null ? job.getBuildDurationMs() : 0L,
                Instant.now()
        );

        DomainEvent<BuildCompletedEvent> event = DomainEvent.of(
                "BuildCompletedEvent",
                job.getDeploymentId(),
                job.getDeploymentId(),
                payload
        );

        log.info("Publishing BuildCompletedEvent for deployment: {}", job.getDeploymentId());
        kafkaTemplate.send(KafkaTopics.BUILD_EVENTS, job.getDeploymentId().toString(), event);
    }

    @Override
    public void publishBuildFailed(BuildJob job, String reason) {
        BuildFailedEvent payload = new BuildFailedEvent(
                job.getId(),
                job.getDeploymentId(),
                job.getProjectId(),
                reason,
                job.getBuildLogs(),
                Instant.now()
        );

        DomainEvent<BuildFailedEvent> event = DomainEvent.of(
                "BuildFailedEvent",
                job.getDeploymentId(),
                job.getDeploymentId(),
                payload
        );

        log.info("Publishing BuildFailedEvent for deployment: {}", job.getDeploymentId());
        kafkaTemplate.send(KafkaTopics.BUILD_EVENTS, job.getDeploymentId().toString(), event);
    }
}
