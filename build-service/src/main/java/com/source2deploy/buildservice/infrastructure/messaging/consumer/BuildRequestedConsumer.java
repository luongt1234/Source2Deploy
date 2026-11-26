package com.source2deploy.buildservice.infrastructure.messaging.consumer;

import com.source2deploy.buildservice.application.service.BuildApplicationService;
import com.source2deploy.event.DomainEvent;
import com.source2deploy.event.KafkaTopics;
import com.source2deploy.event.build.BuildRequestedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BuildRequestedConsumer {

    private static final Logger log = LoggerFactory.getLogger(BuildRequestedConsumer.class);
    private final BuildApplicationService buildService;

    public BuildRequestedConsumer(BuildApplicationService buildService) {
        this.buildService = buildService;
    }

    @KafkaListener(topics = KafkaTopics.BUILD_EVENTS, groupId = "build-service-group")
    public void handleBuildRequested(DomainEvent<BuildRequestedEvent> event) {
        if ("BuildRequestedEvent".equals(event.eventType()) && event.payload() != null) {
            BuildRequestedEvent payload = event.payload();
            log.info("Received BuildRequestedEvent for deployment: {}", payload.deploymentId());
            buildService.triggerBuild(payload.deploymentId(), payload.projectId(), payload.repositoryUrl(), payload.commitHash());
        }
    }
}
