package com.source2deploy.deployservice.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deployServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Deploy Service API")
                        .description("Application Deployment and Release Management Microservice")
                        .version("1.0.0")
                        .contact(new Contact().name("Source2Deploy Team")));
    }
}
