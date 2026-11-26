package com.source2deploy.gateway;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayServiceApplicationTests {
    private static final HttpServer upstream = startUpstream();

    @LocalServerPort int port;
    @MockBean ReactiveJwtDecoder decoder;

    @DynamicPropertySource
    static void config(DynamicPropertyRegistry config) {
        config.add("AUTH_SERVICE_URL", () -> "http://127.0.0.1:" + upstream.getAddress().getPort());
    }

    @AfterAll
    static void stopUpstream() {
        upstream.stop(0);
    }

    @Test
    void enforcesRoutePermissionAndForwardsBearerAndCorrelationHeaders() {
        var http = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
        when(decoder.decode("allowed")).thenReturn(Mono.just(jwt("allowed", "identity:read")));
        when(decoder.decode("limited")).thenReturn(Mono.just(jwt("limited", "openid")));

        http.get().uri("/api/v1/users/me").exchange().expectStatus().isUnauthorized();
        http.get().uri("/api/v1/users/me").headers(headers -> headers.setBearerAuth("limited"))
                .exchange().expectStatus().isForbidden();

        http.get().uri("/api/v1/users/me").headers(headers -> {
            headers.setBearerAuth("allowed");
            headers.setOrigin("http://localhost:5173");
            headers.set("X-Correlation-ID", "gateway-integration");
        }).exchange().expectStatus().isOk()
                .expectHeader().valueEquals("X-Correlation-ID", "gateway-integration")
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173")
                .expectBody(String.class).isEqualTo("Bearer allowed|gateway-integration|null");
    }

    private Jwt jwt(String value, String scope) {
        return Jwt.withTokenValue(value).header("alg", "RS256").subject("local-test-user")
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300))
                .claim("scope", scope).claim("roles", List.of("USER"))
                .claim("permissions", List.of("identity:read")).build();
    }

    private static HttpServer startUpstream() {
        try {
            var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/api/v1/users/me", exchange -> {
                try (exchange) {
                    var headers = exchange.getRequestHeaders();
                    byte[] body = String.join("|", headers.getFirst(HttpHeaders.AUTHORIZATION),
                            headers.getFirst("X-Correlation-ID"), headers.getFirst(HttpHeaders.ORIGIN))
                            .getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, body.length);
                    exchange.getResponseBody().write(body);
                }
            });
            server.start();
            return server;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not start the test upstream", ex);
        }
    }
}
