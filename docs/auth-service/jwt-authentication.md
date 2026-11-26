# JWT bearer authentication

Send an access JWT in the `Authorization: Bearer <access_token>` header. The access JWT has an RS256 signature, stable user UUID in `sub`, Auth Service `iss`, API `aud`, expiration, scopes, roles and permissions. Resource Servers validate the signature, issuer, audience, expiry, optional `nbf` and the access-token marker before mapping authorities. The ID token authenticates the user to the OIDC client.

```mermaid
sequenceDiagram
    participant C as Client
    participant A as Auth Service
    C->>A: GET /api/v1/users/me with bearer access JWT
    A->>A: Validate signature, claims and identity:read authorization
    A->>A: Load active account by subject
    A-->>C: Current user profile
```

```mermaid
sequenceDiagram
    participant C as Client
    participant G as Gateway
    participant B as Backend
    participant A as Auth Service JWKS
    C->>G: GET /api/v1/projects with bearer access JWT
    opt Gateway key cache miss
        G->>A: Fetch public signing keys
    end
    G->>G: Validate JWT and route permission
    G->>B: Forward bearer access JWT
    opt Backend key cache miss
        B->>A: Fetch public signing keys
    end
    B->>B: Validate JWT and route permission
    B-->>G: Project response
    G-->>C: Project response
```

Set each consumer's public `AUTH_ISSUER` and `AUTH_AUDIENCE` to the exact issuance values. `AUTH_JWKS_URI` can point to an internal HTTPS or otherwise trusted JWKS endpoint. Spring caches the keys and refreshes when needed. Unauthenticated or invalid tokens receive 401. Valid tokens without the operation's scope, role or permission receive 403.

The offline checks deliberately use a five-minute default access lifetime. Revocation and introspection affect central token state immediately. Previously issued JWTs presented to offline validators can still be accepted until expiration and allowed clock skew.
