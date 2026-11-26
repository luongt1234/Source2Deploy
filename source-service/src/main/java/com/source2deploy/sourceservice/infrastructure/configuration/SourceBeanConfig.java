package com.source2deploy.sourceservice.infrastructure.configuration;

import com.source2deploy.sourceservice.application.port.output.SourceEventPublisherPort;
import com.source2deploy.sourceservice.application.service.SourceApplicationService;
import com.source2deploy.sourceservice.domain.repository.SourceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SourceBeanConfig {

    @Bean
    public SourceApplicationService sourceApplicationService(
            SourceRepository sourceRepository,
            SourceEventPublisherPort eventPublisherPort) {
        return new SourceApplicationService(sourceRepository, eventPublisherPort);
    }
}
