package com.source2deploy.monitoringservice.infrastructure.configuration;

import com.source2deploy.monitoringservice.application.service.MonitoringApplicationService;
import com.source2deploy.monitoringservice.domain.repository.DeploymentLogRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitoringBeanConfig {

    @Bean
    public MonitoringApplicationService monitoringApplicationService(DeploymentLogRepository repository) {
        return new MonitoringApplicationService(repository);
    }
}
