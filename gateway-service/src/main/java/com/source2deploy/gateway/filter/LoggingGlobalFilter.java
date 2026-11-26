package com.source2deploy.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Global filter for request tracing and logging across microservices.
 * Generates and propagates a correlation ID (X-Correlation-ID) for downstream microservices.
 */
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingGlobalFilter.class);
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        // Retrieve existing correlation ID or generate a new one
        String correlationId = headers.getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        final String finalCorrelationId = correlationId;
        long startTime = System.currentTimeMillis();

        // Spring Security supplies read-only request headers after checking the request.
        HttpHeaders downstreamHeaders = new HttpHeaders();
        downstreamHeaders.addAll(headers);
        downstreamHeaders.set(CORRELATION_ID_HEADER, finalCorrelationId);
        var mutatedRequest = new ServerHttpRequestDecorator(request) {
            @Override
            public HttpHeaders getHeaders() {
                return downstreamHeaders;
            }
        };

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        log.info("[{}] --> {} {} from {}",
                finalCorrelationId,
                request.getMethod(),
                request.getURI().getPath(),
                request.getRemoteAddress());

        var response = mutatedExchange.getResponse();
        response.beforeCommit(() -> {
            response.getHeaders().set(CORRELATION_ID_HEADER, finalCorrelationId);
            return Mono.empty();
        });
        return chain.filter(mutatedExchange)
                .doOnSuccess(ignored -> log.info("[{}] <-- {} {} returned {} in {} ms",
                        finalCorrelationId, request.getMethod(), request.getURI().getPath(),
                        response.getStatusCode(), System.currentTimeMillis() - startTime));
    }

    @Override
    public int getOrder() {
        // Run early in filter chain to ensure all downstream filters have the correlation ID
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
