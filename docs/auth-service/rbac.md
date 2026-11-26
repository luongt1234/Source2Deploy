# Roles, permissions and scopes

Accounts have many roles and roles have many permissions. Registration assigns the seeded `USER` role. `ADMIN` is assigned through trusted database operations and has every seeded permission. Initial roles and permissions live in `V1__identity.sql`.

| Operation | Required scope and permission |
| --- | --- |
| Read own account and permissions | `identity:read` |
| List or fetch projects | `project:read` |
| Create a project | `project:create` |
| Read source analysis jobs | `source:read` |
| Trigger source analysis | `source:clone` |
| Read deployment, build, infrastructure, registry, release or monitoring data | `deployment:read` |
| Create or cancel a deployment | `deployment:create` or `deployment:cancel` |

The Gateway and backend require a recognized user role, the matching signed permission and the matching OAuth scope. The scope comes from the client's registered configuration and the user's authorization. The permission comes from the user's database roles at issuance. The current-user endpoint looks up the account by the JWT subject and responds with the account's current state.

These permissions are platform-wide operation grants. Project and deployment records currently have no tenant or owner identity field. Per-object ownership rules belong in those business services if tenant boundaries are introduced.

For an authorized role assignment, insert into `auth_user_role` using the user's UUID and the selected `auth_role.id`, and coordinate role removals with the configured access-token lifetime.
