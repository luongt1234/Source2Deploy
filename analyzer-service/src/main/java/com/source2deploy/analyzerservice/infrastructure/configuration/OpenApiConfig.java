package com.source2deploy.analyzerservice.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI analyzerServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Analyzer Service API")
                        .description("Source Code Analysis and Configuration Generation Microservice")
                        .version("1.0.0")
                        .contact(new Contact().name("Source2Deploy Team")));
    }
}
