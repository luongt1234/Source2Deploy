package com.source2deploy.auth.infrastructure.config;

import java.net.URI;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration(proxyBeanMethods = false)
class OAuthPersistence {
    static final Set<String> SCOPES = Set.of("openid", "profile", "email", "identity:read",
            "project:read", "project:create", "project:update", "project:delete", "source:read", "source:clone",
            "deployment:create", "deployment:read", "deployment:cancel");

    @Bean
    RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbc) {
        return new JdbcRegisteredClientRepository(jdbc);
    }

    @Bean
    @DependsOnDatabaseInitialization
    OAuth2AuthorizationService authorizationService(JdbcTemplate jdbc, RegisteredClientRepository clients) {
        return new JdbcOAuth2AuthorizationService(jdbc, clients);
    }

    @Bean
    OAuth2AuthorizationConsentService consentService(JdbcTemplate jdbc, RegisteredClientRepository clients) {
        return new JdbcOAuth2AuthorizationConsentService(jdbc, clients);
    }

    @Bean
    ApplicationRunner provisionClients(AuthSettings settings, RegisteredClientRepository clients,
                                       JdbcTemplate jdbc, PlatformTransactionManager transactions,
                                       Environment environment) {
        return args -> new TransactionTemplate(transactions).executeWithoutResult(status -> {
            jdbc.execute("SELECT pg_advisory_xact_lock(1943721940)");
            if (settings.clients() != null) {
                var ids = new java.util.HashSet<String>();
                for (var client : settings.clients()) {
                    if (!ids.add(client.id())) {
                        throw new IllegalArgumentException("Duplicate configured OAuth client id");
                    }
                    saveClient(settings, client, clients, jdbc, environment.acceptsProfiles(Profiles.of("dev & !prod")));
                }
            }
        });
    }

    private void saveClient(AuthSettings settings, AuthSettings.Client client, RegisteredClientRepository repository,
                            JdbcTemplate jdbc, boolean development) {
        if (client.id() == null || !client.id().matches("[A-Za-z0-9_.-]{1,100}")
                || client.redirectUris() == null || client.redirectUris().isEmpty()
                || client.scopes() == null || client.scopes().isEmpty() || !SCOPES.containsAll(client.scopes())) {
            throw new IllegalArgumentException("Invalid OAuth client configuration");
        }
        client.redirectUris().forEach(uri -> validateRedirect(uri, development));
        var logoutUris = client.postLogoutRedirectUris() == null ? Set.<String>of() : client.postLogoutRedirectUris();
        logoutUris.forEach(uri -> validateRedirect(uri, development));

        RegisteredClient existing = repository.findByClientId(client.id());
        var builder = RegisteredClient.withId(existing == null ? UUID.randomUUID().toString() : existing.getId())
                .clientId(client.id())
                .clientIdIssuedAt(existing == null ? Instant.now() : existing.getClientIdIssuedAt())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUris(uris -> uris.addAll(client.redirectUris()))
                .postLogoutRedirectUris(uris -> uris.addAll(logoutUris))
                .scopes(scopes -> scopes.addAll(client.scopes()))
                .clientSettings(ClientSettings.builder().requireProofKey(true)
                        .requireAuthorizationConsent(client.consent()).build())
                .tokenSettings(TokenSettings.builder().reuseRefreshTokens(false)
                        .accessTokenTimeToLive(settings.accessLifetime())
                        .refreshTokenTimeToLive(settings.refreshLifetime())
                        .authorizationCodeTimeToLive(settings.codeLifetime()).build());
        if (client.secretHash() == null || client.secretHash().isBlank()) {
            builder.clientAuthenticationMethod(ClientAuthenticationMethod.NONE);
        } else {
            if (!client.secretHash().matches("\\{bcrypt}\\$2[aby]\\$1[2-6]\\$[./A-Za-z0-9]{53}")) {
                throw new IllegalArgumentException("Client secret must be a delegated BCrypt hash with cost 12–16");
            }
            builder.clientSecret(client.secretHash())
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
        }
        RegisteredClient configured = builder.build();
        if (!configured.equals(existing)) {
            if (existing != null) {
                TokenTransactions.lockClient(jdbc, existing.getId());
                jdbc.update("DELETE FROM oauth2_authorization WHERE registered_client_id = ?", existing.getId());
                jdbc.update("DELETE FROM oauth2_authorization_consent WHERE registered_client_id = ?", existing.getId());
            }
            repository.save(configured);
        }
    }

    private void validateRedirect(String value, boolean development) {
        var uri = URI.create(value);
        boolean loopback = "localhost".equals(uri.getHost()) || "127.0.0.1".equals(uri.getHost())
                || "[::1]".equals(uri.getHost());
        if (uri.getHost() == null || uri.getFragment() != null || uri.getUserInfo() != null
                || value.contains("*") || value.contains(",")
                || !("https".equals(uri.getScheme()) || development && loopback && "http".equals(uri.getScheme()))) {
            throw new IllegalArgumentException("OAuth redirects require an exact HTTPS URI or a dev loopback HTTP URI");
        }
    }
}
