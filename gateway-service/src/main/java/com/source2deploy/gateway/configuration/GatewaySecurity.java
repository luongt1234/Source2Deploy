package com.source2deploy.gateway.configuration;

import java.util.List;

import com.source2deploy.security.AccessTokens;
import com.source2deploy.security.ApiPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration(proxyBeanMethods = false)
@EnableWebFluxSecurity
class GatewaySecurity {
    @Bean
    SecurityWebFilterChain gatewayFilterChain(ServerHttpSecurity http,
                                             UrlBasedCorsConfigurationSource corsConfiguration) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfiguration))
                .requestCache(ServerHttpSecurity.RequestCacheSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges -> {
                    exchanges.pathMatchers(HttpMethod.GET, "/actuator/health", "/fallback/health", "/fallback/default").permitAll();
                    for (var rule : ApiPermissions.RULES) {
                        exchanges.pathMatchers(HttpMethod.valueOf(rule.method()), rule.path())
                                .access((authentication, context) -> authentication
                                        .map(token -> new AuthorizationDecision(AccessTokens.permits(token, rule.permission())))
                                        .defaultIfEmpty(new AuthorizationDecision(false)));
                    }
                    exchanges.anyExchange().denyAll();
                })
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.jwtAuthenticationConverter(
                        new ReactiveJwtAuthenticationConverterAdapter(AccessTokens.authentication()))))
                .build();
    }

    @Bean
    ReactiveJwtDecoder reactiveJwtDecoder(@Value("${security.jwt.issuer}") String issuer,
                                          @Value("${security.jwt.audience}") String audience,
                                          @Value("${security.jwt.jwks-uri}") String jwks) {
        return AccessTokens.reactiveDecoder(issuer, audience, jwks);
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfiguration(
            @Value("${security.cors-origins}") List<String> origins) {
        var cors = new CorsConfiguration();
        cors.setAllowedOrigins(origins);
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Correlation-ID"));
        cors.setExposedHeaders(List.of("X-Correlation-ID"));
        cors.setMaxAge(3600L);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}
