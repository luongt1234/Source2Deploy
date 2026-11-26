package com.source2deploy.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

public final class AccessTokens {
    private AccessTokens() {}

    public static OAuth2TokenValidator<Jwt> validator(String issuer, String audience) {
        return new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer),
                new JwtClaimValidator<List<String>>("aud", values -> values != null && values.contains(audience)),
                new JwtClaimValidator<>("exp", value -> value != null),
                new JwtClaimValidator<String>("token_use", "access"::equals));
    }

    public static NimbusJwtDecoder servletDecoder(String issuer, String audience, String jwksUri) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
        decoder.setJwtValidator(validator(issuer, audience));
        return decoder;
    }

    public static NimbusReactiveJwtDecoder reactiveDecoder(String issuer, String audience, String jwksUri) {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withJwkSetUri(jwksUri).build();
        decoder.setJwtValidator(validator(issuer, audience));
        return decoder;
    }

    public static Converter<Jwt, AbstractAuthenticationToken> authentication() {
        var scopes = new JwtGrantedAuthoritiesConverter();
        return jwt -> {
            var authorities = new ArrayList<>(scopes.convert(jwt));
            addClaim(jwt, "roles", "ROLE_", authorities);
            addClaim(jwt, "permissions", "PERM_", authorities);
            return new JwtAuthenticationToken(jwt, authorities);
        };
    }

    public static boolean permits(org.springframework.security.core.Authentication authentication, String permission) {
        if (!(authentication instanceof JwtAuthenticationToken) || !authentication.isAuthenticated()) {
            return false;
        }
        var authorities = authentication.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority).collect(java.util.stream.Collectors.toSet());
        return authorities.contains("SCOPE_" + permission) && authorities.contains("PERM_" + permission)
                && (authorities.contains("ROLE_USER") || authorities.contains("ROLE_ADMIN"));
    }

    private static void addClaim(Jwt jwt, String name, String prefix,
                                 List<org.springframework.security.core.GrantedAuthority> authorities) {
        if (jwt.getClaims().get(name) instanceof List<?> values) {
            values.stream().filter(String.class::isInstance).map(String.class::cast)
                    .filter(value -> !value.isBlank())
                    .map(value -> new SimpleGrantedAuthority(prefix + value))
                    .forEach(authorities::add);
        }
    }
}
