package com.source2deploy.projectservice.infrastructure.configuration;

import com.source2deploy.projectservice.application.port.output.ProjectEventPublisherPort;
import com.source2deploy.projectservice.application.service.ProjectApplicationService;
import com.source2deploy.projectservice.domain.repository.ProjectRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration wiring clean architecture dependencies using constructor injection.
 */
@Configuration
public class ProjectBeanConfig {

    @Bean
    public ProjectApplicationService projectApplicationService(
            ProjectRepository projectRepository,
            ProjectEventPublisherPort eventPublisherPort) {
        return new ProjectApplicationService(projectRepository, eventPublisherPort);
    }
}
