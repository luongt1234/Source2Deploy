package com.source2deploy.monitoringservice.infrastructure.messaging.consumer;

import com.source2deploy.event.DomainEvent;
import com.source2deploy.event.KafkaTopics;
import com.source2deploy.monitoringservice.application.service.MonitoringApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UnifiedMonitoringConsumer {

    private static final Logger log = LoggerFactory.getLogger(UnifiedMonitoringConsumer.class);
    private final MonitoringApplicationService service;

    public UnifiedMonitoringConsumer(MonitoringApplicationService service) {
        this.service = service;
    }

    @KafkaListener(topics = {
            KafkaTopics.DEPLOYMENT_EVENTS,
            KafkaTopics.ANALYSIS_EVENTS,
            KafkaTopics.BUILD_EVENTS,
            KafkaTopics.REGISTRY_EVENTS,
            KafkaTopics.INFRASTRUCTURE_EVENTS,
            KafkaTopics.DEPLOY_EVENTS
    }, groupId = "monitoring-service-group")
    public void captureDomainEvent(DomainEvent<?> event) {
        log.info("Monitoring audit event: [{}] type: {} aggregate: {}",
                event.eventId(), event.eventType(), event.aggregateId());

        service.recordLog(
                event.aggregateId(),
                "EventBus",
                "INFO",
                "Captured event: " + event.eventType() + " at " + event.occurredAt()
        );
    }
}
