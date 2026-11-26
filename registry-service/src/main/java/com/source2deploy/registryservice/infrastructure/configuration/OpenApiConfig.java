package com.source2deploy.registryservice.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI registryServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Registry Service API")
                        .description("AWS ECR Container Registry Management Microservice")
                        .version("1.0.0")
                        .contact(new Contact().name("Source2Deploy Team")));
    }
}
