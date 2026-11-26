package com.source2deploy.sourceservice.infrastructure.messaging.producer;

import com.source2deploy.event.DomainEvent;
import com.source2deploy.sourceservice.application.port.output.SourceEventPublisherPort;
import com.source2deploy.sourceservice.domain.model.SourceSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaSourceEventPublisher implements SourceEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaSourceEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaSourceEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishSourceReady(SourceSnapshot snapshot) {
        DomainEvent<Map<String, Object>> event = DomainEvent.of(
                "SourceFetchedEvent",
                snapshot.getId(),
                snapshot.getProjectId(),
                Map.of(
                        "sourceId", snapshot.getId(),
                        "projectId", snapshot.getProjectId(),
                        "repositoryUrl", snapshot.getRepositoryUrl(),
                        "branch", snapshot.getBranch(),
                        "commitHash", snapshot.getCommitHash() != null ? snapshot.getCommitHash() : "",
                        "storagePath", snapshot.getStoragePath() != null ? snapshot.getStoragePath() : ""
                )
        );

        log.info("Publishing SourceFetchedEvent for source ID: {}", snapshot.getId());
        kafkaTemplate.send("source.events", snapshot.getId().toString(), event);
    }
}
