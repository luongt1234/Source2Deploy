package com.source2deploy.deployservice.infrastructure.configuration;

import com.source2deploy.deployservice.application.port.output.DeployEventPublisherPort;
import com.source2deploy.deployservice.application.service.DeployApplicationService;
import com.source2deploy.deployservice.domain.repository.ApplicationReleaseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeployBeanConfig {

    @Bean
    public DeployApplicationService deployApplicationService(
            ApplicationReleaseRepository repository,
            DeployEventPublisherPort publisher) {
        return new DeployApplicationService(repository, publisher);
    }
}
