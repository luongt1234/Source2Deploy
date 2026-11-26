package com.source2deploy.auth.infrastructure.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.source2deploy.auth.application.service.Accounts;
import com.source2deploy.security.AccessTokens;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.oauth2.server.authorization.authentication.ClientSecretAuthenticationProvider;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationValidator;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration(proxyBeanMethods = false)
class AuthorizationServerConfig {
    @Bean
    @Order(1)
    SecurityFilterChain authorizationEndpoints(HttpSecurity http, TokenTransactions tokens, AttemptLimits limits)
            throws Exception {
        var server = new OAuth2AuthorizationServerConfigurer();
        var unsupported = new OrRequestMatcher(new AntPathRequestMatcher("/oauth2/device_authorization"),
                new AntPathRequestMatcher("/oauth2/device_verification"));
        var endpoints = new AndRequestMatcher(server.getEndpointsMatcher(), new NegatedRequestMatcher(unsupported));
        return http.securityMatcher(endpoints)
                .with(server, oauth -> oauth
                        .clientAuthentication(client -> client.authenticationProviders(providers ->
                                providers.forEach(provider -> {
                                    if (provider instanceof ClientSecretAuthenticationProvider secrets) {
                                        secrets.setPasswordEncoder(new DelegatingPasswordEncoder("bcrypt",
                                                Map.of("bcrypt", new BCryptPasswordEncoder(12))));
                                    }
                                })))
                        .authorizationEndpoint(endpoint -> endpoint.authenticationProviders(providers ->
                                providers.forEach(provider -> {
                                    if (provider instanceof OAuth2AuthorizationCodeRequestAuthenticationProvider code) {
                                        code.setAuthenticationValidator(context -> {
                                            OAuth2AuthorizationCodeRequestAuthenticationToken request =
                                                    context.getAuthentication();
                                            if (request.getRedirectUri() == null || !context.getRegisteredClient()
                                                    .getRedirectUris().contains(request.getRedirectUri())) {
                                                throw new OAuth2AuthorizationCodeRequestAuthenticationException(
                                                        new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
                                                                "Invalid redirect_uri", null), null);
                                            }
                                            OAuth2AuthorizationCodeRequestAuthenticationValidator
                                                    .DEFAULT_REDIRECT_URI_VALIDATOR.accept(context);
                                            OAuth2AuthorizationCodeRequestAuthenticationValidator
                                                    .DEFAULT_SCOPE_VALIDATOR.accept(context);
                                        });
                                    }
                                })))
                        .authorizationServerMetadataEndpoint(metadata -> metadata
                                .authorizationServerMetadataCustomizer(builder -> builder.claims(claims -> {
                                    claims.remove("device_authorization_endpoint");
                                    claims.put("grant_types_supported", List.of("authorization_code", "refresh_token"));
                                    claims.put("token_endpoint_auth_methods_supported",
                                            List.of("none", "client_secret_basic"));
                                })))
                        .tokenEndpoint(endpoint -> endpoint.authenticationProviders(tokens::wrap))
                        .tokenRevocationEndpoint(endpoint -> endpoint.authenticationProviders(tokens::wrap))
                        .oidc(oidc -> oidc.providerConfigurationEndpoint(endpoint -> endpoint
                                .providerConfigurationCustomizer(builder -> builder.claims(claims -> {
                                    claims.put("grant_types_supported", List.of("authorization_code", "refresh_token"));
                                    claims.put("token_endpoint_auth_methods_supported",
                                            List.of("none", "client_secret_basic"));
                                })))))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpoints))
                .cors(Customizer.withDefaults())
                .addFilterAfter(limits.forOAuth(), CsrfFilter.class)
                .exceptionHandling(errors -> errors.authenticationEntryPoint(browserLoginEntryPoint()))
                .oauth2ResourceServer(resource -> resource.jwt(Customizer.withDefaults()))
                .build();
    }

    private AuthenticationEntryPoint browserLoginEntryPoint() {
        var html = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);
        var login = new AndRequestMatcher(new AntPathRequestMatcher("/oauth2/authorize"), html);
        var entryPoints = new LinkedHashMap<RequestMatcher, AuthenticationEntryPoint>();
        entryPoints.put(login, new LoginUrlAuthenticationEntryPoint("/login"));
        var entryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
        entryPoint.setDefaultEntryPoint(new BearerTokenAuthenticationEntryPoint());
        return entryPoint;
    }

    @Bean
    AuthorizationServerSettings authorizationServerSettings(AuthSettings settings, Environment environment) {
        if (environment.acceptsProfiles(Profiles.of("dev & prod"))) {
            throw new IllegalStateException("The dev and prod profiles are mutually exclusive");
        }
        URI issuer = URI.create(settings.issuer());
        boolean development = environment.acceptsProfiles(Profiles.of("dev & !prod"));
        if (issuer.getHost() == null || issuer.getUserInfo() != null || issuer.getQuery() != null
                || issuer.getFragment() != null || issuer.getPath() == null || !issuer.getPath().isEmpty()
                || !("https".equals(issuer.getScheme()) || development && "http".equals(issuer.getScheme()))) {
            throw new IllegalArgumentException("Configure an HTTPS issuer origin without a trailing slash");
        }
        if (settings.audience() == null || settings.audience().isBlank()) {
            throw new IllegalArgumentException("Configure an access token audience");
        }
        return AuthorizationServerSettings.builder().issuer(settings.issuer()).build();
    }

    @Bean
    JwtDecoder jwtDecoder(JWKSource<SecurityContext> keys, AuthSettings settings) {
        var decoder = (NimbusJwtDecoder) OAuth2AuthorizationServerConfiguration.jwtDecoder(keys);
        decoder.setJwtValidator(AccessTokens.validator(settings.issuer(), settings.audience()));
        return decoder;
    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> tokenClaims(RSAKey key, AuthSettings settings, Accounts accounts) {
        return context -> {
            context.getJwsHeader().keyId(key.getKeyID());
            String subject = context.getPrincipal().getName();
            try {
                var account = accounts.forSession(subject, context.getPrincipal().getAuthorities().stream()
                        .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));
                if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                    var authorities = Accounts.authorities(account);
                    context.getClaims().audience(new ArrayList<>(List.of(settings.audience())))
                            .claim("token_use", "access")
                            .claim("scope", String.join(" ", context.getAuthorizedScopes()))
                            .claim("roles", new ArrayList<>(authorities.roles()))
                            .claim("permissions", new ArrayList<>(authorities.permissions()));
                } else {
                    if (context.getAuthorizedScopes().contains(OidcScopes.PROFILE)) {
                        context.getClaims().claim("preferred_username", account.username());
                    }
                    if (context.getAuthorizedScopes().contains(OidcScopes.EMAIL)) {
                        context.getClaims().claim("email", account.email()).claim("email_verified", account.emailVerified());
                    }
                }
            } catch (Accounts.InactiveAccount ex) {
                throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT);
            }
        };
    }
}
