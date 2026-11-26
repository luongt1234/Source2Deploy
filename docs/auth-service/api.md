# Auth Service API

## Account requests

JSON request bodies require `Content-Type: application/json`.

| Method and path | Authorization | Request and success response |
| --- | --- | --- |
| `POST /api/v1/auth/register` | Public | `username`, `email`, `password` → 201 with account profile. |
| `GET /api/v1/users/me` | Bearer, `identity:read` | 200 with `id`, `username`, `email`, `emailVerified` and `createdAt`. |
| `GET /api/v1/users/me/permissions` | Bearer, `identity:read` | 200 with `roles` and `permissions` arrays. |
| `POST /api/v1/auth/request-verification` | Public, recovery enabled | `email` → 202. |
| `POST /api/v1/auth/verify-email` | Public, recovery enabled | `token` → 204. |
| `POST /api/v1/auth/forgot-password` | Public, recovery enabled | `email` → 202. |
| `POST /api/v1/auth/reset-password` | Public, recovery enabled | `token`, `password` → 204. |

Malformed account requests return 400. Duplicate registration returns a generic 409. Throttled requests return 429 with a `Retry-After` header. Use the Auth Service origin for registration, browser navigation and protocol endpoints. Current-user requests are available through the Auth Service or the Gateway.

## Spring protocol endpoints

| Method and path | Purpose |
| --- | --- |
| `GET /login`, `POST /login` | Browser form and CSRF-protected submission. |
| `POST /logout` | Local browser session logout with CSRF token. |
| `GET /oauth2/authorize` | Start Authorization Code with PKCE. Consent uses the same endpoint. |
| `POST /oauth2/token` | Form-encoded code exchange or confidential refresh. |
| `POST /oauth2/revoke` | Revoke a token using confidential client credentials. |
| `POST /oauth2/introspect` | Query central token state using confidential client credentials. |
| `GET /.well-known/openid-configuration` | OpenID Provider discovery. |
| `GET /.well-known/oauth-authorization-server` | OAuth server metadata. |
| `GET /oauth2/jwks` | Public signing keys. |
| `GET /userinfo`, `POST /userinfo` | OIDC claims scoped to the access token. |
| `GET /connect/logout`, `POST /connect/logout` | OIDC RP-Initiated Logout. |
| `GET /actuator/health` | Service health. |
