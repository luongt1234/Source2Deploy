-- Spring Authorization Server 1.3.3 JDBC schema, adapted for PostgreSQL.
CREATE TABLE oauth2_registered_client (
    id varchar(100) NOT NULL,
    client_id varchar(100) NOT NULL,
    client_id_issued_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret varchar(200) DEFAULT NULL,
    client_secret_expires_at timestamptz DEFAULT NULL,
    client_name varchar(200) NOT NULL,
    client_authentication_methods varchar(1000) NOT NULL,
    authorization_grant_types varchar(1000) NOT NULL,
    redirect_uris varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris varchar(1000) DEFAULT NULL,
    scopes varchar(1000) NOT NULL,
    client_settings varchar(2000) NOT NULL,
    token_settings varchar(2000) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE oauth2_authorization (
    id varchar(100) NOT NULL,
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorization_grant_type varchar(100) NOT NULL,
    authorized_scopes varchar(1000) DEFAULT NULL,
    attributes text DEFAULT NULL,
    state varchar(500) DEFAULT NULL,
    authorization_code_value text DEFAULT NULL,
    authorization_code_issued_at timestamptz DEFAULT NULL,
    authorization_code_expires_at timestamptz DEFAULT NULL,
    authorization_code_metadata text DEFAULT NULL,
    access_token_value text DEFAULT NULL,
    access_token_issued_at timestamptz DEFAULT NULL,
    access_token_expires_at timestamptz DEFAULT NULL,
    access_token_metadata text DEFAULT NULL,
    access_token_type varchar(100) DEFAULT NULL,
    access_token_scopes varchar(1000) DEFAULT NULL,
    oidc_id_token_value text DEFAULT NULL,
    oidc_id_token_issued_at timestamptz DEFAULT NULL,
    oidc_id_token_expires_at timestamptz DEFAULT NULL,
    oidc_id_token_metadata text DEFAULT NULL,
    refresh_token_value text DEFAULT NULL,
    refresh_token_issued_at timestamptz DEFAULT NULL,
    refresh_token_expires_at timestamptz DEFAULT NULL,
    refresh_token_metadata text DEFAULT NULL,
    user_code_value text DEFAULT NULL,
    user_code_issued_at timestamptz DEFAULT NULL,
    user_code_expires_at timestamptz DEFAULT NULL,
    user_code_metadata text DEFAULT NULL,
    device_code_value text DEFAULT NULL,
    device_code_issued_at timestamptz DEFAULT NULL,
    device_code_expires_at timestamptz DEFAULT NULL,
    device_code_metadata text DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE oauth2_authorization_consent (
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorities varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

ALTER TABLE oauth2_registered_client ADD CONSTRAINT oauth2_client_public_id_unique UNIQUE (client_id);
ALTER TABLE oauth2_authorization ADD FOREIGN KEY (registered_client_id)
    REFERENCES oauth2_registered_client(id) ON DELETE CASCADE;
ALTER TABLE oauth2_authorization_consent ADD FOREIGN KEY (registered_client_id)
    REFERENCES oauth2_registered_client(id) ON DELETE CASCADE;

CREATE INDEX oauth2_authorization_client_idx ON oauth2_authorization(registered_client_id);
CREATE INDEX oauth2_authorization_principal_idx ON oauth2_authorization(principal_name);
CREATE INDEX oauth2_authorization_state_idx ON oauth2_authorization(state);
CREATE INDEX oauth2_authorization_code_idx ON oauth2_authorization USING hash (authorization_code_value);
CREATE INDEX oauth2_authorization_access_idx ON oauth2_authorization USING hash (access_token_value);
CREATE INDEX oauth2_authorization_id_token_idx ON oauth2_authorization USING hash (oidc_id_token_value);
CREATE INDEX oauth2_authorization_refresh_idx ON oauth2_authorization USING hash (refresh_token_value);

ALTER TABLE oauth2_authorization ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE TABLE auth_rotated_refresh (
    token_hash VARCHAR(64) PRIMARY KEY,
    authorization_id VARCHAR(100) NOT NULL REFERENCES oauth2_authorization(id) ON DELETE CASCADE,
    expires_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX auth_rotated_refresh_authorization_idx ON auth_rotated_refresh(authorization_id);
CREATE INDEX auth_rotated_refresh_expiry_idx ON auth_rotated_refresh(expires_at);
