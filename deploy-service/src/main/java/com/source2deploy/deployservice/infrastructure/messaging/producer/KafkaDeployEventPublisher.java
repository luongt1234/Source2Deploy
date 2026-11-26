package com.source2deploy.deployservice.infrastructure.messaging.producer;

import com.source2deploy.deployservice.application.port.output.DeployEventPublisherPort;
import com.source2deploy.deployservice.domain.model.ApplicationRelease;
import com.source2deploy.event.DomainEvent;
import com.source2deploy.event.KafkaTopics;
import com.source2deploy.event.deployment.DeploymentCompletedEvent;
import com.source2deploy.event.deployment.DeploymentFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class KafkaDeployEventPublisher implements DeployEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaDeployEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaDeployEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishDeploymentCompleted(ApplicationRelease release) {
        DeploymentCompletedEvent payload = new DeploymentCompletedEvent(
                release.getDeploymentId(),
                release.getProjectId(),
                release.getLiveUrl(),
                Instant.now()
        );

        DomainEvent<DeploymentCompletedEvent> event = DomainEvent.of(
                "DeploymentCompletedEvent",
                release.getDeploymentId(),
                release.getDeploymentId(),
                payload
        );

        log.info("Publishing DeploymentCompletedEvent for deployment: {}", release.getDeploymentId());
        kafkaTemplate.send(KafkaTopics.DEPLOYMENT_EVENTS, release.getDeploymentId().toString(), event);
    }

    @Override
    public void publishDeploymentFailed(ApplicationRelease release, String reason) {
        DeploymentFailedEvent payload = new DeploymentFailedEvent(
                release.getDeploymentId(),
                release.getProjectId(),
                "APPLICATION_DEPLOYMENT",
                reason,
                Instant.now()
        );

        DomainEvent<DeploymentFailedEvent> event = DomainEvent.of(
                "DeploymentFailedEvent",
                release.getDeploymentId(),
                release.getDeploymentId(),
                payload
        );

        log.info("Publishing DeploymentFailedEvent for deployment: {}", release.getDeploymentId());
        kafkaTemplate.send(KafkaTopics.DEPLOYMENT_EVENTS, release.getDeploymentId().toString(), event);
    }
}
