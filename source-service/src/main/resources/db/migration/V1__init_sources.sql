CREATE TABLE IF NOT EXISTS sources (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    repository_url VARCHAR(512) NOT NULL,
    branch VARCHAR(100) NOT NULL DEFAULT 'main',
    commit_hash VARCHAR(64),
    status VARCHAR(50) NOT NULL,
    storage_path VARCHAR(512),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_sources_project_id ON sources(project_id);
CREATE INDEX IF NOT EXISTS idx_sources_status ON sources(status);
