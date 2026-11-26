package com.source2deploy.registryservice.infrastructure.messaging.consumer;

import com.source2deploy.event.DomainEvent;
import com.source2deploy.event.KafkaTopics;
import com.source2deploy.event.registry.ImagePushRequestedEvent;
import com.source2deploy.registryservice.application.service.RegistryApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ImagePushRequestedConsumer {

    private static final Logger log = LoggerFactory.getLogger(ImagePushRequestedConsumer.class);
    private final RegistryApplicationService service;

    public ImagePushRequestedConsumer(RegistryApplicationService service) {
        this.service = service;
    }

    @KafkaListener(topics = KafkaTopics.REGISTRY_EVENTS, groupId = "registry-service-group")
    public void handleImagePushRequested(DomainEvent<ImagePushRequestedEvent> event) {
        if ("ImagePushRequestedEvent".equals(event.eventType()) && event.payload() != null) {
            ImagePushRequestedEvent payload = event.payload();
            log.info("Received ImagePushRequestedEvent for deployment: {}", payload.deploymentId());
            service.pushImage(payload.deploymentId(), payload.projectId(), payload.repositoryName(), payload.localImageTag());
        }
    }
}
