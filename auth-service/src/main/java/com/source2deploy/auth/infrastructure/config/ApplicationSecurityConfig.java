package com.source2deploy.auth.infrastructure.config;

import java.util.List;

import com.source2deploy.security.AccessTokens;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration(proxyBeanMethods = false)
class ApplicationSecurityConfig {
    @Bean
    @Order(2)
    SecurityFilterChain userApis(HttpSecurity http) throws Exception {
        return http.securityMatcher("/api/v1/users/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me", "/api/v1/users/me/permissions")
                        .access((authentication, context) -> new org.springframework.security.authorization.AuthorizationDecision(
                                AccessTokens.permits(authentication.get(), "identity:read")))
                        .anyRequest().denyAll())
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(resource -> resource.jwt(jwt -> jwt.jwtAuthenticationConverter(AccessTokens.authentication())))
                .build();
    }

    @Bean
    @Order(3)
    SecurityFilterChain browserAndRegistration(HttpSecurity http, AttemptLimits limits) throws Exception {
        return http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/logout", "/error", "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password", "/api/v1/auth/request-verification",
                                "/api/v1/auth/verify-email").permitAll()
                        .anyRequest().denyAll())
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/api/v1/auth/**", "POST")))
                .addFilterAfter(limits.forAccounts(), CsrfFilter.class)
                .formLogin(login -> login.failureUrl("/login?error"))
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").deleteCookies("S2DSESSION"))
                .build();
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource(AuthSettings settings) {
        var config = new CorsConfiguration();
        if (settings.corsOrigins() != null) {
            for (String origin : settings.corsOrigins()) {
                java.net.URI uri = java.net.URI.create(origin);
                if (uri.getHost() == null || uri.getUserInfo() != null || uri.getQuery() != null
                        || uri.getFragment() != null || !"".equals(uri.getPath())
                        || !List.of("http", "https").contains(uri.getScheme())) {
                    throw new IllegalArgumentException("CORS origins must be explicit HTTP(S) origins");
                }
            }
            config.setAllowedOrigins(settings.corsOrigins());
        }
        config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);
        var source = new UrlBasedCorsConfigurationSource();
        for (String path : List.of("/api/v1/**", "/oauth2/token", "/oauth2/jwks", "/userinfo",
                "/.well-known/**")) {
            source.registerCorsConfiguration(path, config);
        }
        return source;
    }
}
