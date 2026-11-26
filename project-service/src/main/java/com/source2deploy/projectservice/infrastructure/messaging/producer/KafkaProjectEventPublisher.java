package com.source2deploy.projectservice.infrastructure.messaging.producer;

import com.source2deploy.event.DomainEvent;
import com.source2deploy.projectservice.application.port.output.ProjectEventPublisherPort;
import com.source2deploy.projectservice.domain.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaProjectEventPublisher implements ProjectEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaProjectEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProjectEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishProjectCreated(Project project) {
        DomainEvent<Map<String, Object>> event = DomainEvent.of(
                "ProjectCreatedEvent",
                project.getId(),
                null,
                Map.of(
                        "projectId", project.getId(),
                        "name", project.getName(),
                        "repositoryUrl", project.getRepositoryUrl(),
                        "defaultBranch", project.getDefaultBranch()
                )
        );

        log.info("Publishing ProjectCreatedEvent for project ID: {}", project.getId());
        kafkaTemplate.send("project.events", project.getId().toString(), event);
    }
}
