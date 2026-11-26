package com.source2deploy.registryservice.infrastructure.configuration;

import com.source2deploy.registryservice.application.port.output.RegistryEventPublisherPort;
import com.source2deploy.registryservice.application.service.RegistryApplicationService;
import com.source2deploy.registryservice.domain.repository.ContainerImageRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistryBeanConfig {

    @Bean
    public RegistryApplicationService registryApplicationService(
            ContainerImageRepository repository,
            RegistryEventPublisherPort publisher) {
        return new RegistryApplicationService(repository, publisher);
    }
}
