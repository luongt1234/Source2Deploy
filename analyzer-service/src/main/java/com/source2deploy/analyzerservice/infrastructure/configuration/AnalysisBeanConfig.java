package com.source2deploy.analyzerservice.infrastructure.configuration;

import com.source2deploy.analyzerservice.application.port.output.AnalysisEventPublisherPort;
import com.source2deploy.analyzerservice.application.service.AnalysisApplicationService;
import com.source2deploy.analyzerservice.domain.repository.AnalysisJobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalysisBeanConfig {

    @Bean
    public AnalysisApplicationService analysisApplicationService(
            AnalysisJobRepository repository,
            AnalysisEventPublisherPort publisher) {
        return new AnalysisApplicationService(repository, publisher);
    }
}
