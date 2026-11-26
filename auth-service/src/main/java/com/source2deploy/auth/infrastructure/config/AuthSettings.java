package com.source2deploy.auth.infrastructure.config;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth")
record AuthSettings(String issuer, String audience, Path signingKey, Path previousKeys,
                    List<String> corsOrigins, Duration accessLifetime, Duration refreshLifetime,
                    Duration codeLifetime, List<Client> clients) {
    record Client(String id, String secretHash, Set<String> redirectUris, Set<String> postLogoutRedirectUris,
                  Set<String> scopes, boolean consent) {}
}
