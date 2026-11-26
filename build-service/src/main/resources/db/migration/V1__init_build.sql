CREATE TABLE IF NOT EXISTS build_jobs (
    id UUID PRIMARY KEY,
    deployment_id UUID NOT NULL,
    project_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    image_tag VARCHAR(255),
    build_duration_ms BIGINT,
    build_logs TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_build_jobs_deployment_id ON build_jobs(deployment_id);
