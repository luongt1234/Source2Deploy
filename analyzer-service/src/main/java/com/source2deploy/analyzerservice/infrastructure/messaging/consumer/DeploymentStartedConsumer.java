package com.source2deploy.analyzerservice.infrastructure.messaging.consumer;

import com.source2deploy.analyzerservice.application.service.AnalysisApplicationService;
import com.source2deploy.event.DomainEvent;
import com.source2deploy.event.KafkaTopics;
import com.source2deploy.event.deployment.DeploymentStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DeploymentStartedConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeploymentStartedConsumer.class);
    private final AnalysisApplicationService analysisService;

    public DeploymentStartedConsumer(AnalysisApplicationService analysisService) {
        this.analysisService = analysisService;
    }

    @KafkaListener(topics = KafkaTopics.DEPLOYMENT_EVENTS, groupId = "analyzer-service-group")
    public void handleDeploymentStarted(DomainEvent<DeploymentStartedEvent> event) {
        if ("DeploymentStartedEvent".equals(event.eventType()) && event.payload() != null) {
            DeploymentStartedEvent payload = event.payload();
            log.info("Received DeploymentStartedEvent for deployment: {}", payload.deploymentId());
            analysisService.analyze(payload.deploymentId(), payload.projectId(), payload.repositoryUrl());
        }
    }
}
