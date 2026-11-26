package com.source2deploy.deploymentservice.infrastructure.configuration;

import java.util.List;

import com.source2deploy.security.BackendResourceSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(BackendResourceSecurity.class)
public class SecurityConfig {
    @Bean
    BackendResourceSecurity.Routes protectedRoutes() {
        return new BackendResourceSecurity.Routes(List.of("/api/v1/deployments"));
    }
}
