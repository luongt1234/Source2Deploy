# Security operations

Use an HTTPS issuer in production and terminate TLS at the deployment edge. If a reverse proxy terminates TLS, configure Spring Boot forwarded headers and have the proxy overwrite incoming forwarding headers. Use the `prod` profile and mount the signing JWK read-only from the deployment secret store. Browser session cookies are Secure, HttpOnly and SameSite=Lax. Login and local logout require CSRF tokens. OAuth protocol endpoints use Spring Authorization Server's own correlation and client validation. The JSON registration and recovery endpoints accept explicit CORS origins and no cross-origin credentials.

## Signing keys

Provision a private RSA JWK with at least 2048 bits, preferably 3072. Use its RFC 7638 thumbprint as the `kid`, or let startup derive it. The `dev` profile generates a local key only when the file is missing. Use a persistent file or volume to retain the issuer's key across restarts.

To rotate, build a public-only JWKS with the new key and the current/retained public keys. First deploy all issuers with their current private key and the next public key alongside retained public keys. Then deploy the new private key and an updated additional JWKS that contains the old public key and any other retained keys. After the latest previously issued access and ID tokens expire including clock skew and the logout window, remove the old public key. Keep key files synchronized across issuer replicas. Public discovery exposes key material through `/oauth2/jwks`.

## Credentials and abuse controls

User passwords use BCrypt at cost 12. Registration requires at least 12 characters and at most 72 UTF-8 bytes. Failed login responses are generic. The rate limiter uses the immediate peer address and username budgets. Apply edge rate limits too if requests arrive through a reverse proxy. Preserve the configured proxy trust boundary before using real client addresses for rate limits.

Enable account recovery only after configuring an SMTP sender. Request endpoints return 202 for valid request shapes regardless of account existence. Each code is single use, stored as a digest and has a fixed expiry. Reset changes the credential version and removes stored OAuth authorizations, so old browser sessions cannot issue tokens.

Confidential clients get rotating refresh tokens. Replaying a previously rotated token during its original validity invalidates the remaining grant. Keep OAuth tables and backups access-controlled and encrypted at rest. Spring's JDBC persistence stores tokens and their metadata for framework revocation, introspection and UserInfo. Avoid request-body, query-string and Authorization-header logging at the edge. Run browsers with session affinity or a shared session store if deploying multiple Auth Service replicas.
