package com.source2deploy.auth.infrastructure.config;

import java.util.List;

import com.source2deploy.auth.infrastructure.security.TokenHashes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenRevocationAuthenticationProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
class TokenTransactions {
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transaction;
    private final OAuth2AuthorizationService authorizations;

    TokenTransactions(JdbcTemplate jdbc, PlatformTransactionManager manager, OAuth2AuthorizationService authorizations) {
        this.jdbc = jdbc;
        this.transaction = new TransactionTemplate(manager);
        this.authorizations = authorizations;
    }

    void wrap(List<AuthenticationProvider> providers) {
        providers.replaceAll(provider -> {
            if (provider instanceof OAuth2AuthorizationCodeAuthenticationProvider
                    || provider instanceof OAuth2RefreshTokenAuthenticationProvider
                    || provider instanceof OAuth2TokenRevocationAuthenticationProvider) {
                return new AuthenticationProvider() {
                    @Override
                    public boolean supports(Class<?> type) {
                        return provider.supports(type);
                    }

                    @Override
                    public Authentication authenticate(Authentication request) {
                        return serialize(provider, request);
                    }
                };
            }
            return provider;
        });
    }

    private Authentication serialize(AuthenticationProvider provider, Authentication request) {
        if (!(request.getPrincipal() instanceof OAuth2ClientAuthenticationToken client)
                || !client.isAuthenticated() || client.getRegisteredClient() == null) {
            return provider.authenticate(request);
        }
        String clientId = client.getRegisteredClient().getId();
        // Serialize transitions across replicas, including revocation racing code exchange or refresh.
        Object result = transaction.execute(status -> {
            lockClient(jdbc, clientId);
            try {
                lockAccount(request);
                return request instanceof OAuth2RefreshTokenAuthenticationToken refresh
                        ? rotate(provider, refresh, clientId) : provider.authenticate(request);
            } catch (OAuth2AuthenticationException ex) {
                // Spring may persist invalidation before reporting a reused code.
                return ex;
            }
        });
        if (result instanceof OAuth2AuthenticationException ex) {
            throw ex;
        }
        return (Authentication) result;
    }

    static void lockClient(JdbcTemplate jdbc, String clientId) {
        jdbc.query("SELECT pg_advisory_xact_lock(hashtextextended(?, 71647))", rs -> { }, clientId);
    }

    private void lockAccount(Authentication request) {
        String token;
        OAuth2TokenType type;
        if (request instanceof OAuth2RefreshTokenAuthenticationToken refresh) {
            token = refresh.getRefreshToken();
            type = OAuth2TokenType.REFRESH_TOKEN;
        } else if (request instanceof org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationToken code) {
            token = code.getCode();
            type = new OAuth2TokenType("code");
        } else {
            return;
        }
        var authorization = authorizations.findByToken(token, type);
        if (authorization != null) {
            jdbc.query("SELECT id FROM auth_user WHERE id = ? FOR SHARE",
                    rs -> { }, java.util.UUID.fromString(authorization.getPrincipalName()));
        }
    }

    private Authentication rotate(AuthenticationProvider provider, OAuth2RefreshTokenAuthenticationToken request,
                                  String clientId) {
        String hash = TokenHashes.sha256(request.getRefreshToken());
        int revoked = jdbc.update("""
                DELETE FROM oauth2_authorization a USING auth_rotated_refresh r
                WHERE r.token_hash = ? AND r.expires_at > now() AND a.id = r.authorization_id
                  AND a.registered_client_id = ?
                """, hash, clientId);
        if (revoked > 0) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT);
        }
        var authorization = authorizations.findByToken(request.getRefreshToken(), OAuth2TokenType.REFRESH_TOKEN);
        Authentication result = provider.authenticate(request);
        if (authorization != null && result != null) {
            jdbc.update("""
                    INSERT INTO auth_rotated_refresh(token_hash, authorization_id, expires_at) VALUES (?, ?, ?)
                    """, hash, authorization.getId(),
                    java.sql.Timestamp.from(authorization.getRefreshToken().getToken().getExpiresAt()));
        }
        return result;
    }
}
