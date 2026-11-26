package com.source2deploy.projectservice.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI projectServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project Service API")
                        .description("Project and Repository Management microservice for Source2Deploy platform")
                        .version("1.0.0")
                        .contact(new Contact().name("Source2Deploy Team")));
    }
}
