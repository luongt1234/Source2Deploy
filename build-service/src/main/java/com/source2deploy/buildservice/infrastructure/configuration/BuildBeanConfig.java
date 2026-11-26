package com.source2deploy.buildservice.infrastructure.configuration;

import com.source2deploy.buildservice.application.port.output.BuildEventPublisherPort;
import com.source2deploy.buildservice.application.service.BuildApplicationService;
import com.source2deploy.buildservice.domain.repository.BuildJobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BuildBeanConfig {

    @Bean
    public BuildApplicationService buildApplicationService(
            BuildJobRepository repository,
            BuildEventPublisherPort publisher) {
        return new BuildApplicationService(repository, publisher);
    }
}
