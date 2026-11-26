package com.source2deploy.sourceservice.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sourceServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Source Service API")
                        .description("Source Code Fetching and Snapshot Management microservice for Source2Deploy platform")
                        .version("1.0.0")
                        .contact(new Contact().name("Source2Deploy Team")));
    }
}
