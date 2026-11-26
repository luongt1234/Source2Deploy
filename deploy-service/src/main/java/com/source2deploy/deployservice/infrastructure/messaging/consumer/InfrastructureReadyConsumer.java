package com.source2deploy.deployservice.infrastructure.messaging.consumer;

import com.source2deploy.deployservice.application.service.DeployApplicationService;
import com.source2deploy.event.DomainEvent;
import com.source2deploy.event.KafkaTopics;
import com.source2deploy.event.infrastructure.InfrastructureReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InfrastructureReadyConsumer {

    private static final Logger log = LoggerFactory.getLogger(InfrastructureReadyConsumer.class);
    private final DeployApplicationService service;

    public InfrastructureReadyConsumer(DeployApplicationService service) {
        this.service = service;
    }

    @KafkaListener(topics = KafkaTopics.INFRASTRUCTURE_EVENTS, groupId = "deploy-service-group")
    public void handleInfrastructureReady(DomainEvent<InfrastructureReadyEvent> event) {
        if ("InfrastructureReadyEvent".equals(event.eventType()) && event.payload() != null) {
            InfrastructureReadyEvent payload = event.payload();
            log.info("Received InfrastructureReadyEvent for deployment: {}", payload.deploymentId());
            service.deploy(
                    payload.deploymentId(),
                    payload.projectId(),
                    "app-image:latest",
                    payload.ecsClusterArn(),
                    payload.targetGroupArn(),
                    payload.albDnsName()
            );
        }
    }
}
