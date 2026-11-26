package com.source2deploy.deploymentservice.infrastructure.messaging.producer;

import com.source2deploy.deploymentservice.application.port.output.DeploymentEventPublisherPort;
import com.source2deploy.deploymentservice.domain.model.Deployment;
import com.source2deploy.event.DomainEvent;
import com.source2deploy.event.KafkaTopics;
import com.source2deploy.event.deployment.DeploymentCompletedEvent;
import com.source2deploy.event.deployment.DeploymentFailedEvent;
import com.source2deploy.event.deployment.DeploymentStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class KafkaDeploymentEventPublisher implements DeploymentEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaDeploymentEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaDeploymentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishDeploymentStarted(Deployment deployment, Map<String, String> envVars) {
        DeploymentStartedEvent payload = new DeploymentStartedEvent(
                deployment.getId(),
                deployment.getProjectId(),
                deployment.getRepositoryUrl(),
                deployment.getBranch(),
                deployment.getCommitHash(),
                envVars,
                Instant.now()
        );

        DomainEvent<DeploymentStartedEvent> event = DomainEvent.of(
                "DeploymentStartedEvent",
                deployment.getId(),
                deployment.getId(),
                payload
        );

        log.info("Publishing DeploymentStartedEvent for deployment: {}", deployment.getId());
        kafkaTemplate.send(KafkaTopics.DEPLOYMENT_EVENTS, deployment.getId().toString(), event);
    }

    @Override
    public void publishDeploymentCompleted(Deployment deployment) {
        DeploymentCompletedEvent payload = new DeploymentCompletedEvent(
                deployment.getId(),
                deployment.getProjectId(),
                deployment.getLiveUrl(),
                Instant.now()
        );

        DomainEvent<DeploymentCompletedEvent> event = DomainEvent.of(
                "DeploymentCompletedEvent",
                deployment.getId(),
                deployment.getId(),
                payload
        );

        log.info("Publishing DeploymentCompletedEvent for deployment: {}", deployment.getId());
        kafkaTemplate.send(KafkaTopics.DEPLOYMENT_EVENTS, deployment.getId().toString(), event);
    }

    @Override
    public void publishDeploymentFailed(Deployment deployment, String failedStage, String reason) {
        DeploymentFailedEvent payload = new DeploymentFailedEvent(
                deployment.getId(),
                deployment.getProjectId(),
                failedStage,
                reason,
                Instant.now()
        );

        DomainEvent<DeploymentFailedEvent> event = DomainEvent.of(
                "DeploymentFailedEvent",
                deployment.getId(),
                deployment.getId(),
                payload
        );

        log.info("Publishing DeploymentFailedEvent for deployment: {}", deployment.getId());
        kafkaTemplate.send(KafkaTopics.DEPLOYMENT_EVENTS, deployment.getId().toString(), event);
    }
}
