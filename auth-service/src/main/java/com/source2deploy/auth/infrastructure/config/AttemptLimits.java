package com.source2deploy.auth.infrastructure.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Map;

import com.source2deploy.auth.application.service.Accounts;
import com.source2deploy.auth.infrastructure.security.TokenHashes;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
class AttemptLimits {
    private static final long WINDOW = 60;
    private final JdbcTemplate jdbc;
    private final Clock clock;
    private final int loginIpLimit;
    private final int loginPairLimit;
    private final int loginAccountLimit;
    private final int registrationLimit;
    private final int tokenLimit;

    AttemptLimits(JdbcTemplate jdbc, Clock clock,
                  @Value("${auth.limits.login-ip:60}") int loginIpLimit,
                  @Value("${auth.limits.login-pair:8}") int loginPairLimit,
                  @Value("${auth.limits.login-account:25}") int loginAccountLimit,
                  @Value("${auth.limits.registration:10}") int registrationLimit,
                  @Value("${auth.limits.token:180}") int tokenLimit) {
        if (Math.min(Math.min(loginIpLimit, loginPairLimit), Math.min(loginAccountLimit,
                Math.min(registrationLimit, tokenLimit))) <= 0) {
            throw new IllegalArgumentException("Rate limits must be positive");
        }
        this.jdbc = jdbc;
        this.clock = clock;
        this.loginIpLimit = loginIpLimit;
        this.loginPairLimit = loginPairLimit;
        this.loginAccountLimit = loginAccountLimit;
        this.registrationLimit = registrationLimit;
        this.tokenLimit = tokenLimit;
    }

    OncePerRequestFilter forAccounts() {
        return filter(Map.of("/login", loginIpLimit, "/api/v1/auth/register", registrationLimit,
                "/api/v1/auth/forgot-password", registrationLimit, "/api/v1/auth/reset-password", registrationLimit,
                "/api/v1/auth/verify-email", registrationLimit, "/api/v1/auth/request-verification", registrationLimit));
    }

    OncePerRequestFilter forOAuth() {
        return filter(Map.of("/oauth2/token", tokenLimit, "/oauth2/revoke", tokenLimit,
                "/oauth2/introspect", tokenLimit));
    }

    private OncePerRequestFilter filter(Map<String, Integer> paths) {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain) throws IOException, ServletException {
                String path = request.getRequestURI().substring(request.getContextPath().length());
                Integer limit = paths.get(path);
                if (!"POST".equals(request.getMethod()) || limit == null) {
                    chain.doFilter(request, response);
                    return;
                }

                String ip = request.getRemoteAddr();
                long now = clock.instant().getEpochSecond();
                long window = now - now % WINDOW;
                boolean allowed = record(path, ip, window, limit);
                if (allowed && "/login".equals(path)) {
                    String username = Accounts.normalize(value(request.getParameter("username")));
                    String password = value(request.getParameter("password"));
                    if (username.length() > 64 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
                        response.sendRedirect(request.getContextPath() + "/login?error");
                        return;
                    }
                    allowed = record("login-pair", ip + "/" + username, window, loginPairLimit)
                            && record("login-account", username, window, loginAccountLimit);
                }
                if (allowed) {
                    chain.doFilter(request, response);
                } else {
                    response.setHeader("Retry-After", Long.toString(window + WINDOW - now));
                    response.sendError(429);
                }
            }
        };
    }

    private boolean record(String purpose, String value, long window, int limit) {
        var counts = jdbc.queryForList("""
                INSERT INTO auth_attempt_window(bucket, window_start, attempts) VALUES (?, ?, 1)
                ON CONFLICT (bucket, window_start) DO UPDATE SET attempts = auth_attempt_window.attempts + 1
                WHERE auth_attempt_window.attempts < ? RETURNING attempts
                """, Integer.class, purpose + ":" + TokenHashes.sha256(value), window, limit);
        return !counts.isEmpty();
    }

    @Scheduled(fixedDelay = 300_000)
    void purgeExpired() {
        jdbc.update("DELETE FROM auth_attempt_window WHERE window_start < ?", clock.instant().getEpochSecond() - WINDOW);
        jdbc.update("DELETE FROM auth_account_token WHERE expires_at <= now()");
        jdbc.update("DELETE FROM auth_rotated_refresh WHERE expires_at <= now()");
        jdbc.update("""
                DELETE FROM oauth2_authorization
                WHERE GREATEST(created_at, authorization_code_expires_at, access_token_expires_at,
                               oidc_id_token_expires_at, refresh_token_expires_at) <= now() - INTERVAL '1 day'
                """);
    }

    private static String value(String input) {
        return input == null ? "" : input;
    }
}
