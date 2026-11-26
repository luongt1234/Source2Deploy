package com.source2deploy.infrastructureservice.infrastructure.messaging.producer;

import com.source2deploy.event.DomainEvent;
import com.source2deploy.event.KafkaTopics;
import com.source2deploy.event.infrastructure.InfrastructureFailedEvent;
import com.source2deploy.event.infrastructure.InfrastructureReadyEvent;
import com.source2deploy.infrastructureservice.application.port.output.InfrastructureEventPublisherPort;
import com.source2deploy.infrastructureservice.domain.model.InfrastructureDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class KafkaInfrastructureEventPublisher implements InfrastructureEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaInfrastructureEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaInfrastructureEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishInfrastructureReady(InfrastructureDeployment deployment) {
        InfrastructureReadyEvent payload = new InfrastructureReadyEvent(
                deployment.getDeploymentId(),
                deployment.getProjectId(),
                deployment.getEcsClusterArn(),
                deployment.getTargetGroupArn(),
                deployment.getAlbDnsName(),
                Instant.now()
        );

        DomainEvent<InfrastructureReadyEvent> event = DomainEvent.of(
                "InfrastructureReadyEvent",
                deployment.getDeploymentId(),
                deployment.getDeploymentId(),
                payload
        );

        log.info("Publishing InfrastructureReadyEvent for deployment: {}", deployment.getDeploymentId());
        kafkaTemplate.send(KafkaTopics.INFRASTRUCTURE_EVENTS, deployment.getDeploymentId().toString(), event);
    }

    @Override
    public void publishInfrastructureFailed(InfrastructureDeployment deployment, String reason) {
        InfrastructureFailedEvent payload = new InfrastructureFailedEvent(
                deployment.getDeploymentId(),
                deployment.getProjectId(),
                reason,
                Instant.now()
        );

        DomainEvent<InfrastructureFailedEvent> event = DomainEvent.of(
                "InfrastructureFailedEvent",
                deployment.getDeploymentId(),
                deployment.getDeploymentId(),
                payload
        );

        log.info("Publishing InfrastructureFailedEvent for deployment: {}", deployment.getDeploymentId());
        kafkaTemplate.send(KafkaTopics.INFRASTRUCTURE_EVENTS, deployment.getDeploymentId().toString(), event);
    }
}
