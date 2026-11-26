package com.source2deploy.analyzerservice.infrastructure.messaging.producer;

import com.source2deploy.analyzerservice.application.port.output.AnalysisEventPublisherPort;
import com.source2deploy.analyzerservice.domain.model.AnalysisJob;
import com.source2deploy.analyzerservice.domain.model.AnalysisResult;
import com.source2deploy.event.DomainEvent;
import com.source2deploy.event.KafkaTopics;
import com.source2deploy.event.analysis.AnalysisCompletedEvent;
import com.source2deploy.event.analysis.AnalysisFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class KafkaAnalysisEventPublisher implements AnalysisEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaAnalysisEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaAnalysisEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishAnalysisCompleted(AnalysisJob job) {
        AnalysisResult res = job.getResult();
        AnalysisCompletedEvent payload = new AnalysisCompletedEvent(
                job.getId(),
                job.getDeploymentId(),
                job.getProjectId(),
                res.detectedLanguage(),
                res.detectedFramework(),
                res.runtimeVersion(),
                res.buildTool(),
                res.port(),
                res.dockerfilePath(),
                Instant.now()
        );

        DomainEvent<AnalysisCompletedEvent> event = DomainEvent.of(
                "AnalysisCompletedEvent",
                job.getDeploymentId(),
                job.getDeploymentId(),
                payload
        );

        log.info("Publishing AnalysisCompletedEvent for deployment: {}", job.getDeploymentId());
        kafkaTemplate.send(KafkaTopics.ANALYSIS_EVENTS, job.getDeploymentId().toString(), event);
    }

    @Override
    public void publishAnalysisFailed(AnalysisJob job, String reason) {
        AnalysisFailedEvent payload = new AnalysisFailedEvent(
                job.getId(),
                job.getDeploymentId(),
                job.getProjectId(),
                reason,
                Instant.now()
        );

        DomainEvent<AnalysisFailedEvent> event = DomainEvent.of(
                "AnalysisFailedEvent",
                job.getDeploymentId(),
                job.getDeploymentId(),
                payload
        );

        log.info("Publishing AnalysisFailedEvent for deployment: {}", job.getDeploymentId());
        kafkaTemplate.send(KafkaTopics.ANALYSIS_EVENTS, job.getDeploymentId().toString(), event);
    }
}
