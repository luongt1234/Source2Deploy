package com.source2deploy.deploymentservice.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deploymentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Deployment Service API")
                        .description("Deployment Orchestration and Workflow Management microservice for Source2Deploy")
                        .version("1.0.0")
                        .contact(new Contact().name("Source2Deploy Team")));
    }
}
