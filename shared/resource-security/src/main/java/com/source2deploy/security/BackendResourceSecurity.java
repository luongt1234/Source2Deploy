package com.source2deploy.security;

import java.util.List;

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
public class BackendResourceSecurity {
    public record Routes(List<String> prefixes) {}

    @Bean
    SecurityFilterChain backendFilterChain(HttpSecurity http, Routes routes) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> {
                    requests.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
                    requests.requestMatchers(HttpMethod.GET, "/actuator/health", "/v3/api-docs/**",
                            "/swagger-ui/**", "/swagger-ui.html").permitAll();
                    for (var rule : ApiPermissions.RULES) {
                        if (routes.prefixes().stream().anyMatch(rule.path()::startsWith)) {
                            requests.requestMatchers(HttpMethod.valueOf(rule.method()), rule.path())
                                    .access((authentication, context) -> new AuthorizationDecision(
                                            AccessTokens.permits(authentication.get(), rule.permission())));
                        }
                    }
                    requests.anyRequest().denyAll();
                })
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.jwtAuthenticationConverter(AccessTokens.authentication())))
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${security.jwt.issuer}") String issuer,
                          @Value("${security.jwt.audience}") String audience,
                          @Value("${security.jwt.jwks-uri}") String jwks) {
        return AccessTokens.servletDecoder(issuer, audience, jwks);
    }
}
