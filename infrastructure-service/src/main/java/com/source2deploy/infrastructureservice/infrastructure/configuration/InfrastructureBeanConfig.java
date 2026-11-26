package com.source2deploy.infrastructureservice.infrastructure.configuration;

import com.source2deploy.infrastructureservice.application.port.output.InfrastructureEventPublisherPort;
import com.source2deploy.infrastructureservice.application.service.InfrastructureApplicationService;
import com.source2deploy.infrastructureservice.domain.repository.InfrastructureDeploymentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureBeanConfig {

    @Bean
    public InfrastructureApplicationService infrastructureApplicationService(
            InfrastructureDeploymentRepository repository,
            InfrastructureEventPublisherPort publisher) {
        return new InfrastructureApplicationService(repository, publisher);
    }
}
