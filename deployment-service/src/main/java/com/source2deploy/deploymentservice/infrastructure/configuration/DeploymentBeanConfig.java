package com.source2deploy.deploymentservice.infrastructure.configuration;

import com.source2deploy.deploymentservice.application.port.output.DeploymentEventPublisherPort;
import com.source2deploy.deploymentservice.application.service.DeploymentApplicationService;
import com.source2deploy.deploymentservice.domain.repository.DeploymentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeploymentBeanConfig {

    @Bean
    public DeploymentApplicationService deploymentApplicationService(
            DeploymentRepository deploymentRepository,
            DeploymentEventPublisherPort eventPublisherPort) {
        return new DeploymentApplicationService(deploymentRepository, eventPublisherPort);
    }
}
