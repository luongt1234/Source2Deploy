package com.source2deploy.registryservice.infrastructure.messaging.producer;

import com.source2deploy.event.DomainEvent;
import com.source2deploy.event.KafkaTopics;
import com.source2deploy.event.registry.ImagePushedEvent;
import com.source2deploy.registryservice.application.port.output.RegistryEventPublisherPort;
import com.source2deploy.registryservice.domain.model.ContainerImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class KafkaRegistryEventPublisher implements RegistryEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaRegistryEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaRegistryEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishImagePushed(ContainerImage image) {
        ImagePushedEvent payload = new ImagePushedEvent(
                image.getDeploymentId(),
                image.getProjectId(),
                image.getRepositoryName(),
                image.getImageDigest(),
                image.getFullImageUrl(),
                Instant.now()
        );

        DomainEvent<ImagePushedEvent> event = DomainEvent.of(
                "ImagePushedEvent",
                image.getDeploymentId(),
                image.getDeploymentId(),
                payload
        );

        log.info("Publishing ImagePushedEvent for deployment: {}", image.getDeploymentId());
        kafkaTemplate.send(KafkaTopics.REGISTRY_EVENTS, image.getDeploymentId().toString(), event);
    }
}
