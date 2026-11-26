package com.source2deploy.gateway.filter;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Custom GatewayFilterFactory to enforce Authorization header presence
 * on protected routes.
 *
 * Usage in application.yml:
 * filters:
 *   - name: AuthValidation
 *     args:
 *       enabled: true
 */
@Component
public class AuthValidationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<AuthValidationGatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(AuthValidationGatewayFilterFactory.class);

    public AuthValidationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!config.isEnabled()) {
                return chain.filter(exchange);
            }

            HttpHeaders headers = exchange.getRequest().getHeaders();
            String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Unauthorized access attempt to: {}", exchange.getRequest().getURI().getPath());
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            // In production, token decoding and validation can be done here or in auth service
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    @Getter
    @Setter
    public static class Config {
        private boolean enabled = true;
    }
}
