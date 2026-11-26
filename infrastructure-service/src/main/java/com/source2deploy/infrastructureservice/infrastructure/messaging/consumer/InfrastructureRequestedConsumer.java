package com.source2deploy.infrastructureservice.infrastructure.messaging.consumer;

import com.source2deploy.event.DomainEvent;
import com.source2deploy.event.KafkaTopics;
import com.source2deploy.event.infrastructure.InfrastructureRequestedEvent;
import com.source2deploy.infrastructureservice.application.service.InfrastructureApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InfrastructureRequestedConsumer {

    private static final Logger log = LoggerFactory.getLogger(InfrastructureRequestedConsumer.class);
    private final InfrastructureApplicationService service;

    public InfrastructureRequestedConsumer(InfrastructureApplicationService service) {
        this.service = service;
    }

    @KafkaListener(topics = KafkaTopics.INFRASTRUCTURE_EVENTS, groupId = "infrastructure-service-group")
    public void handleInfrastructureRequested(DomainEvent<InfrastructureRequestedEvent> event) {
        if ("InfrastructureRequestedEvent".equals(event.eventType()) && event.payload() != null) {
            InfrastructureRequestedEvent payload = event.payload();
            log.info("Received InfrastructureRequestedEvent for deployment: {}", payload.deploymentId());
            service.provision(payload.deploymentId(), payload.projectId(), payload.environment(), payload.containerPort());
        }
    }
}
