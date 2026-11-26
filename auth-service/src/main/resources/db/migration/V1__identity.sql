CREATE TABLE auth_user (
    id UUID PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE CHECK (username = lower(username)),
    email VARCHAR(254) NOT NULL UNIQUE CHECK (email = lower(email)),
    password_hash VARCHAR(255) NOT NULL,
    credential_version UUID NOT NULL,
    enabled BOOLEAN NOT NULL,
    email_verified BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);
CREATE TABLE auth_role (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);
CREATE TABLE auth_permission (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);
CREATE TABLE auth_user_role (
    user_id UUID REFERENCES auth_user(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES auth_role(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);
CREATE INDEX auth_user_role_role_idx ON auth_user_role(role_id);
CREATE TABLE auth_role_permission (
    role_id BIGINT REFERENCES auth_role(id) ON DELETE CASCADE,
    permission_id BIGINT REFERENCES auth_permission(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);
CREATE INDEX auth_role_permission_permission_idx ON auth_role_permission(permission_id);

INSERT INTO auth_role (name, description) VALUES ('USER', 'Standard user'), ('ADMIN', 'Platform administrator');
INSERT INTO auth_permission (name, description) VALUES
    ('identity:read', 'Read own account and permissions'),
    ('project:create', 'Create projects'),
    ('project:read', 'Read projects'),
    ('project:update', 'Update projects'),
    ('project:delete', 'Delete projects'),
    ('source:clone', 'Trigger source analysis'),
    ('source:read', 'Read source analysis jobs'),
    ('deployment:create', 'Create deployments'),
    ('deployment:read', 'Read deployment activity and artifacts'),
    ('deployment:cancel', 'Cancel deployments');

INSERT INTO auth_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM auth_role r CROSS JOIN auth_permission p
WHERE r.name = 'ADMIN' OR p.name IN ('identity:read', 'project:create', 'project:read', 'source:read',
                                     'deployment:create', 'deployment:read', 'deployment:cancel');

CREATE TABLE auth_attempt_window (
    bucket VARCHAR(150) NOT NULL,
    window_start BIGINT NOT NULL,
    attempts INT NOT NULL,
    PRIMARY KEY (bucket, window_start)
);
CREATE INDEX auth_attempt_window_expiry_idx ON auth_attempt_window(window_start);

CREATE TABLE auth_account_token (
    token_hash VARCHAR(64) PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth_user(id) ON DELETE CASCADE,
    purpose VARCHAR(20) NOT NULL CHECK (purpose IN ('VERIFY_EMAIL', 'RESET_PASSWORD')),
    issued_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMPTZ NOT NULL,
    UNIQUE (user_id, purpose)
);
CREATE INDEX auth_account_token_expiry_idx ON auth_account_token(expires_at);
