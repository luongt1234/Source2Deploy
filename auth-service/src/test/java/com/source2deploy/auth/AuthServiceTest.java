package com.source2deploy.auth;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"auth.recovery.enabled=true", "auth.recovery.from=identity@example.test", "spring.mail.host=localhost"})
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Testcontainers
class AuthServiceTest {
    private static final String ISSUER = "http://localhost:8089";
    private static final String AUDIENCE = "source2deploy-api";
    private static final String PUBLIC = "test-web";
    private static final String CONFIDENTIAL = "test-bff";
    private static final String REDIRECT = "http://127.0.0.1:5173/callback";
    private static final String SECRET = "local-test-client-secret";
    private static final String PASSWORD = "test password with words!";
    private static final String VERIFIER = "ynWbc9T9ag1zmCUcD6G3fdhf76vv7HkWlOo5zvLwPUk";

    private static RSAKey previousSigningKey;

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void config(DynamicPropertyRegistry config) throws Exception {
        config.add("auth.limits.registration", () -> "100");
        config.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        config.add("spring.datasource.username", POSTGRES::getUsername);
        config.add("spring.datasource.password", POSTGRES::getPassword);
        config.add("auth.signing-key", () -> Path.of("target", "test-signing.key").toString());
        previousSigningKey = new RSAKeyGenerator(2048).keyIDFromThumbprint(true).generate();
        Path previousJwks = Path.of("target", "test-previous.jwks");
        Files.writeString(previousJwks, new JWKSet(previousSigningKey.toPublicJWK()).toString());
        config.add("auth.previous-keys", previousJwks::toString);
        configureClient(config, "auth.clients[0]", PUBLIC);
        configureClient(config, "auth.clients[1]", CONFIDENTIAL);
        configureClient(config, "auth.clients[2]", "consent-web");
        config.add("auth.clients[2].consent", () -> "true");
        String hash = "{bcrypt}" + new BCryptPasswordEncoder(12).encode(SECRET);
        config.add("auth.clients[1].secret-hash", () -> hash);
    }

    private static void configureClient(DynamicPropertyRegistry config, String prefix, String id) {
        config.add(prefix + ".id", () -> id);
        config.add(prefix + ".redirect-uris", () -> REDIRECT);
        config.add(prefix + ".scopes", () -> "openid,email,profile,identity:read,project:read");
    }

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;
    @Autowired RSAKey signingKey;
    @MockBean JavaMailSender mail;

    @Test
    void registrationHashesPasswordsAndEnforcesCaseInsensitiveUniqueness() throws Exception {
        String user = username();
        String email = user + "@example.test";
        register(user.toUpperCase(java.util.Locale.ROOT), email, PASSWORD)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(user))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.emailVerified").value(false));
        register(user, "another-" + email, PASSWORD).andExpect(status().isConflict());
        register(username(), email.toUpperCase(java.util.Locale.ROOT), PASSWORD).andExpect(status().isConflict());
        register(username(), "weak-" + email, "weak").andExpect(status().isBadRequest());
        register(username(), "long-" + email, "ế".repeat(25)).andExpect(status().isBadRequest());
        String hash = jdbc.queryForObject("select password_hash from auth_user where username = ?", String.class, user);
        assertThat(hash).isNotEqualTo(PASSWORD);
        assertThat(new BCryptPasswordEncoder().matches(PASSWORD, hash)).isTrue();
    }

    @Test
    void loginRequiresCsrfAndRejectsInvalidCredentialsAndDisabledAccounts() throws Exception {
        String user = registerUser();
        mvc.perform(post("/login").param("username", user).param("password", PASSWORD))
                .andExpect(status().isForbidden());
        mvc.perform(post("/login").with(csrf()).param("username", user).param("password", "wrong"))
                .andExpect(redirectedUrl("/login?error"));
        jdbc.update("UPDATE auth_user SET enabled = false WHERE username = ?", user);
        mvc.perform(post("/login").with(csrf()).param("username", user).param("password", PASSWORD))
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void publishesDiscoveryAndJwksAndCompletesPublicOidcFlow() throws Exception {
        mvc.perform(get("/.well-known/openid-configuration"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.issuer").value(ISSUER))
                .andExpect(jsonPath("$.userinfo_endpoint").value(ISSUER + "/userinfo"));
        mvc.perform(get("/oauth2/jwks")).andExpect(status().isOk())
                .andExpect(jsonPath("$.keys[0].kid").value(signingKey.getKeyID()))
                .andExpect(jsonPath("$.keys[0].d").doesNotExist())
                .andExpect(jsonPath("$.keys[0].p").doesNotExist())
                .andExpect(jsonPath("$.keys[1].kid").value(previousSigningKey.getKeyID()))
                .andExpect(jsonPath("$.keys[1].d").doesNotExist());
        mvc.perform(authorize(PUBLIC, REDIRECT, "S256").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("**/login"));

        String user = registerUser();
        String code = code(PUBLIC, login(user));
        JsonNode tokens = response(exchange(PUBLIC, code, VERIFIER, null).andExpect(status().isOk()));
        assertThat(tokens.has("refresh_token")).isFalse();
        String access = tokens.get("access_token").asText();
        var accessClaims = SignedJWT.parse(access).getJWTClaimsSet();
        assertThat(accessClaims.getAudience()).containsExactly(AUDIENCE);
        assertThat(accessClaims.getStringListClaim("roles")).containsExactly("USER");
        assertThat(accessClaims.getClaims()).doesNotContainKeys("email", "preferred_username");
        var id = SignedJWT.parse(tokens.get("id_token").asText()).getJWTClaimsSet();
        assertThat(id.getSubject()).isEqualTo(accessClaims.getSubject());
        assertThat(id.getAudience()).containsExactly(PUBLIC);
        assertThat(id.getStringClaim("nonce")).isEqualTo("nonce-test");
        mvc.perform(bearer("/userinfo", access)).andExpect(status().isOk())
                .andExpect(jsonPath("$.sub").value(id.getSubject()))
                .andExpect(jsonPath("$.email").value(user + "@example.test"))
                .andExpect(jsonPath("$.preferred_username").value(user));
        mvc.perform(bearer("/api/v1/users/me", access)).andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user));
        mvc.perform(bearer("/api/v1/users/me/permissions", access)).andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("USER"));
        mvc.perform(bearer("/api/v1/users/me", tokens.get("id_token").asText()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsInvalidRedirectPkceAndCodes() throws Exception {
        MockHttpSession session = login(registerUser());
        mvc.perform(authorize(PUBLIC, "http://127.0.0.1:9999/callback", "S256").session(session))
                .andExpect(status().isBadRequest()).andExpect(header().doesNotExist("Location"));
        var plain = mvc.perform(authorize(PUBLIC, REDIRECT, "plain").session(session)).andReturn().getResponse();
        assertThat(plain.getStatus()).isBetween(300, 499);
        assertThat(java.util.Objects.toString(plain.getHeader("Location"), "")).doesNotContain("code=");
        var noChallenge = mvc.perform(get("/oauth2/authorize").session(session).queryParam("response_type", "code")
                .queryParam("client_id", PUBLIC).queryParam("redirect_uri", REDIRECT).queryParam("scope", "openid"))
                .andReturn().getResponse();
        assertThat(noChallenge.getStatus()).isBetween(300, 499);
        assertThat(java.util.Objects.toString(noChallenge.getHeader("Location"), "")).doesNotContain("code=");
        String code = code(PUBLIC, session);
        exchange(PUBLIC, code, null, null).andExpect(status().is4xxClientError());
        exchange(PUBLIC, code, "wrong-" + VERIFIER, null).andExpect(status().is4xxClientError());
        exchange(PUBLIC, "invalid-code", VERIFIER, null).andExpect(status().is4xxClientError());
        exchange(PUBLIC, code, VERIFIER, null).andExpect(status().isOk());
        exchange(PUBLIC, code, VERIFIER, null).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_grant"));
        String expired = code(PUBLIC, session);
        jdbc.update("UPDATE oauth2_authorization SET authorization_code_issued_at = now() - interval '2 minutes', authorization_code_expires_at = now() - interval '1 minute' WHERE authorization_code_value = ?", expired);
        exchange(PUBLIC, expired, VERIFIER, null).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_grant"));
    }

    @Test
    void confidentialClientsRotateRefreshTokensAndRejectReuseAndRevokedGrants() throws Exception {
        MockHttpSession session = login(registerUser());
        String code = code(CONFIDENTIAL, session);
        exchange(CONFIDENTIAL, code, VERIFIER, "wrong-secret").andExpect(status().isUnauthorized());
        JsonNode first = response(exchange(CONFIDENTIAL, code, VERIFIER, SECRET).andExpect(status().isOk()));
        String refresh = first.get("refresh_token").asText();
        JsonNode rotated = response(refresh(refresh).andExpect(status().isOk()));
        String next = rotated.get("refresh_token").asText();
        assertThat(next).isNotEqualTo(refresh);
        refresh(refresh).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("invalid_grant"));
        refresh(next).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("invalid_grant"));

        JsonNode second = response(exchange(CONFIDENTIAL, code(CONFIDENTIAL, session), VERIFIER, SECRET)
                .andExpect(status().isOk()));
        String access = second.get("access_token").asText();
        introspect(access).andExpect(jsonPath("$.active").value(true));
        mvc.perform(post("/oauth2/revoke").with(httpBasic(CONFIDENTIAL, SECRET)).param("token", access))
                .andExpect(status().isOk());
        introspect(access).andExpect(jsonPath("$.active").value(false));
        String revokedRefresh = second.get("refresh_token").asText();
        mvc.perform(post("/oauth2/revoke").with(httpBasic(CONFIDENTIAL, SECRET)).param("token", revokedRefresh))
                .andExpect(status().isOk());
        refresh(revokedRefresh).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("invalid_grant"));
    }

    @Test
    void bearerRequiresTrustedSignatureIssuerAudienceTimeScopeRoleAndPermission() throws Exception {
        String user = registerUser();
        String subject = jdbc.queryForObject("select id::text from auth_user where username = ?", String.class, user);
        String valid = signed(signingKey, subject, ISSUER, AUDIENCE, 300, -60, "identity:read", List.of("USER"), List.of("identity:read"));
        mvc.perform(bearer("/api/v1/users/me", valid)).andExpect(status().isOk());
        String previous = signed(previousSigningKey, subject, ISSUER, AUDIENCE, 300, -60,
                "identity:read", List.of("USER"), List.of("identity:read"));
        mvc.perform(bearer("/api/v1/users/me", previous)).andExpect(status().isOk());
        RSAKey wrong = new RSAKeyGenerator(2048).keyID(signingKey.getKeyID()).generate();
        for (String invalid : List.of(
                signed(wrong, subject, ISSUER, AUDIENCE, 300, -60, "identity:read", List.of("USER"), List.of("identity:read")),
                signed(signingKey, subject, "https://wrong.example.test", AUDIENCE, 300, -60, "identity:read", List.of("USER"), List.of("identity:read")),
                signed(signingKey, subject, ISSUER, "wrong-api", 300, -60, "identity:read", List.of("USER"), List.of("identity:read")),
                signed(signingKey, subject, ISSUER, AUDIENCE, -120, -180, "identity:read", List.of("USER"), List.of("identity:read")),
                signed(signingKey, subject, ISSUER, AUDIENCE, 300, 180, "identity:read", List.of("USER"), List.of("identity:read")))) {
            mvc.perform(bearer("/api/v1/users/me", invalid)).andExpect(status().isUnauthorized());
        }
        for (String unauthorized : List.of(
                signed(signingKey, subject, ISSUER, AUDIENCE, 300, -60, "openid", List.of("USER"), List.of("identity:read")),
                signed(signingKey, subject, ISSUER, AUDIENCE, 300, -60, "identity:read", List.of(), List.of("identity:read")),
                signed(signingKey, subject, ISSUER, AUDIENCE, 300, -60, "identity:read", List.of("USER"), List.of()))) {
            mvc.perform(bearer("/api/v1/users/me", unauthorized)).andExpect(status().isForbidden());
        }
        mvc.perform(get("/api/v1/users/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void recordsConsentBeforeReturningTheAuthorizationCode() throws Exception {
        String user = registerUser();
        MockHttpSession session = login(user);
        var consent = mvc.perform(authorize("consent-web", REDIRECT, "S256").session(session))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var state = java.util.regex.Pattern.compile("name=\"state\" value=\"([^\"]+)\"").matcher(consent);
        assertThat(state.find()).isTrue();
        mvc.perform(post("/oauth2/authorize").session(session)
                .param("client_id", "consent-web").param("state", state.group(1))
                .param("scope", "identity:read"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern(REDIRECT + "?code=**"));
        assertThat(jdbc.queryForObject("""
                SELECT count(*) FROM oauth2_authorization_consent c
                JOIN oauth2_registered_client r ON c.registered_client_id = r.id
                WHERE r.client_id = 'consent-web'
                """, Integer.class)).isEqualTo(1);
    }

    @Test
    void accountCodesAreSingleUseAndResetRevokesTheRefreshGrant() throws Exception {
        String user = registerUser();
        String email = user + "@example.test";
        MockHttpSession originalSession = login(user);
        JsonNode tokens = response(exchange(CONFIDENTIAL, code(CONFIDENTIAL, originalSession), VERIFIER, SECRET)
                .andExpect(status().isOk()));
        String access = tokens.get("access_token").asText();
        String refresh = tokens.get("refresh_token").asText();
        String verification = requestAccountCode("/api/v1/auth/request-verification", email);
        mvc.perform(accountPost("/api/v1/auth/verify-email", Map.of("token", verification)))
                .andExpect(status().isNoContent());
        mvc.perform(accountPost("/api/v1/auth/verify-email", Map.of("token", verification)))
                .andExpect(status().isBadRequest());
        mvc.perform(bearer("/api/v1/users/me", access)).andExpect(jsonPath("$.emailVerified").value(true));

        org.mockito.Mockito.clearInvocations(mail);
        String reset = requestAccountCode("/api/v1/auth/forgot-password", email);
        var resetBody = Map.of("token", reset, "password", "replacement test password!");
        mvc.perform(accountPost("/api/v1/auth/reset-password", resetBody)).andExpect(status().isNoContent());
        mvc.perform(accountPost("/api/v1/auth/reset-password", resetBody)).andExpect(status().isBadRequest());
        refresh(refresh).andExpect(status().isBadRequest());
        mvc.perform(post("/login").with(csrf()).param("username", user).param("password", PASSWORD))
                .andExpect(redirectedUrl("/login?error"));
        mvc.perform(post("/login").with(csrf()).param("username", user).param("password", resetBody.get("password")))
                .andExpect(redirectedUrl("/"));
        exchange(PUBLIC, code(PUBLIC, originalSession), VERIFIER, null).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_grant"));
    }

    private String requestAccountCode(String path, String email) throws Exception {
        mvc.perform(accountPost(path, Map.of("email", email))).andExpect(status().isAccepted());
        var message = ArgumentCaptor.forClass(SimpleMailMessage.class);
        org.mockito.Mockito.verify(mail, org.mockito.Mockito.timeout(5000)).send(message.capture());
        String content = message.getValue().getText();
        String code = content.substring(content.lastIndexOf('\n') + 1);
        assertThat(code).matches("[A-Za-z0-9_-]{43}");
        return code;
    }

    private MockHttpServletRequestBuilder accountPost(String path, Map<String, String> body) throws Exception {
        return post(path).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(body));
    }

    private String signed(RSAKey key, String sub, String iss, String aud, long expiration, long notBefore,
                          String scope, List<String> roles, List<String> permissions) throws Exception {
        Instant now = Instant.now();
        var jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build(),
                new JWTClaimsSet.Builder().issuer(iss).subject(sub).audience(aud)
                        .expirationTime(Date.from(now.plusSeconds(expiration))).notBeforeTime(Date.from(now.plusSeconds(notBefore)))
                        .claim("token_use", "access").claim("scope", scope).claim("roles", roles)
                        .claim("permissions", permissions).build());
        jwt.sign(new RSASSASigner(key));
        return jwt.serialize();
    }

    private String username() {
        return "test-" + UUID.randomUUID();
    }

    private String registerUser() throws Exception {
        String user = username();
        register(user, user + "@example.test", PASSWORD).andExpect(status().isCreated());
        return user;
    }

    private ResultActions register(String user, String email, String password) throws Exception {
        return mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(Map.of("username", user, "email", email, "password", password))));
    }

    private MockHttpSession login(String user) throws Exception {
        var result = mvc.perform(post("/login").with(csrf()).param("username", user).param("password", PASSWORD))
                .andExpect(redirectedUrl("/")).andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    private MockHttpServletRequestBuilder authorize(String client, String redirect, String method) throws Exception {
        String challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(
                MessageDigest.getInstance("SHA-256").digest(VERIFIER.getBytes(StandardCharsets.US_ASCII)));
        return get("/oauth2/authorize").queryParam("response_type", "code").queryParam("client_id", client)
                .queryParam("redirect_uri", redirect).queryParam("state", "test-state").queryParam("nonce", "nonce-test")
                .queryParam("scope", "openid email profile identity:read project:read")
                .queryParam("code_challenge", challenge).queryParam("code_challenge_method", method);
    }

    private String code(String client, MockHttpSession session) throws Exception {
        String location = mvc.perform(authorize(client, REDIRECT, "S256").session(session))
                .andExpect(status().is3xxRedirection()).andReturn().getResponse().getRedirectedUrl();
        var query = UriComponentsBuilder.fromUri(URI.create(location)).build().getQueryParams();
        assertThat(query.getFirst("state")).isEqualTo("test-state");
        assertThat(query.getFirst("code")).isNotBlank();
        return query.getFirst("code");
    }

    private ResultActions exchange(String client, String code, String verifier, String secret) throws Exception {
        var request = post("/oauth2/token").param("grant_type", "authorization_code")
                .param("code", code).param("redirect_uri", REDIRECT);
        if (verifier != null) {
            request.param("code_verifier", verifier);
        }
        return mvc.perform(secret == null ? request.param("client_id", client) : request.with(httpBasic(client, secret)));
    }

    private ResultActions refresh(String token) throws Exception {
        return mvc.perform(post("/oauth2/token").with(httpBasic(CONFIDENTIAL, SECRET))
                .param("grant_type", "refresh_token").param("refresh_token", token));
    }

    private ResultActions introspect(String token) throws Exception {
        return mvc.perform(post("/oauth2/introspect").with(httpBasic(CONFIDENTIAL, SECRET)).param("token", token))
                .andExpect(status().isOk());
    }

    private MockHttpServletRequestBuilder bearer(String path, String token) {
        return get(path).header("Authorization", "Bearer " + token);
    }

    private JsonNode response(ResultActions result) throws Exception {
        return json.readTree(result.andReturn().getResponse().getContentAsByteArray());
    }
}
