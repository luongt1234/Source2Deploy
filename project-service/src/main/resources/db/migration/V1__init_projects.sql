CREATE TABLE IF NOT EXISTS projects (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    repository_url VARCHAR(512) NOT NULL,
    default_branch VARCHAR(100) NOT NULL DEFAULT 'main',
    status VARCHAR(50) NOT NULL,
    github_connection_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_projects_repository_url ON projects(repository_url);
CREATE INDEX IF NOT EXISTS idx_projects_status ON projects(status);
