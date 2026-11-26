# Source2Deploy Auth Service

The Auth Service owns accounts, login, email verification, password reset, OAuth clients, consent and token issuance. It uses Spring Authorization Server 1.3.3 with the existing Java 21 and Spring Boot 3.3.5 Maven reactor. Consumers validate signed access JWTs using Spring Security Resource Server.

## Local development

```sh
docker compose up --build --wait auth-service
curl -fsS http://localhost:8089/actuator/health
curl -fsS http://localhost:8089/.well-known/openid-configuration
```

Compose runs an isolated PostgreSQL 16 instance for auth and applies Flyway migrations at startup. Its database and development signing key live in separate named volumes. The auth port binds to loopback. Run `docker compose up --build --wait` for Gateway and backend routes, with the Gateway on port 8080. The public development client is `source2deploy-web` with the callback `http://localhost:5173/callback`. Run an OIDC client at that origin, or set its exact callback in `AUTH_DEV_REDIRECT_URIS`.

For a host Java run, provide a PostgreSQL instance with an empty `auth_db`, set `AUTH_DB_URL`, `AUTH_DB_USERNAME` and `AUTH_DB_PASSWORD`, and run from the repository root.

```sh
mvn -pl auth-service -am package -DskipTests
java -jar auth-service/target/auth-service-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

## Configuration

| Setting | Purpose and defaults |
| --- | --- |
| `SERVER_PORT` | Auth HTTP listen port, default 8089. |
| `AUTH_PORT` | Compose host port. Match the external URL in `AUTH_ISSUER`. |
| `AUTH_DB_URL`, `AUTH_DB_USERNAME`, `AUTH_DB_PASSWORD` | PostgreSQL connection. Compose supplies local values. |
| `AUTH_ISSUER` | Public issuer origin with no trailing slash. Dev defaults to `http://localhost:8089`. |
| `AUTH_AUDIENCE` | Expected API audience, default `source2deploy-api`. |
| `AUTH_SIGNING_KEY`, `AUTH_PREVIOUS_KEYS` | Private RSA JWK file and optional public JWKS file. |
| `AUTH_JWKS_URI`, `AUTH_SERVICE_URL` | Consumer key URL and Gateway user-API upstream. Compose uses internal container DNS. |
| `AUTH_ACCESS_LIFETIME`, `AUTH_REFRESH_LIFETIME`, `AUTH_CODE_LIFETIME` | Defaults 5 minutes, 8 hours and 2 minutes. |
| `AUTH_CORS_ORIGINS`, `GATEWAY_CORS_ORIGINS` | Comma-separated frontend origins for the respective audience-facing host. |
| `AUTH_DEV_CLIENT_ID`, `AUTH_DEV_REDIRECT_URIS`, `AUTH_DEV_POST_LOGOUT_URIS`, `AUTH_DEV_CLIENT_CONSENT` | Development client settings. |
| `AUTH_RECOVERY_ENABLED`, `AUTH_MAIL_FROM`, `SPRING_MAIL_HOST` | Enable recovery and provide the sender and SMTP server. Use Spring Boot SMTP settings for authentication, port and TLS. |
| `AUTH_LIMITS_LOGIN_IP`, `AUTH_LIMITS_LOGIN_PAIR`, `AUTH_LIMITS_LOGIN_ACCOUNT`, `AUTH_LIMITS_REGISTRATION`, `AUTH_LIMITS_TOKEN` | Per-minute request budgets, default 60, 8, 25, 10 and 180 respectively. |

## Tests

```sh
mvn test
```

Use a running Docker engine. The auth test configuration uses Docker API 1.44 to support recent Docker engines. For an older daemon, set the API version supported by that daemon using `-Dapi.version`. The Auth Service integration suite starts its own PostgreSQL container and exercises the actual Spring Security and Authorization Server filter chains.

See [architecture](architecture.md), [OAuth flow](oauth-flow.md), [JWT authentication](jwt-authentication.md), [RBAC](rbac.md), [security](security.md) and [API](api.md).
