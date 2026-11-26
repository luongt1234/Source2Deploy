CREATE TABLE IF NOT EXISTS analysis_jobs (
    id UUID PRIMARY KEY,
    deployment_id UUID NOT NULL,
    project_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    detected_language VARCHAR(100),
    detected_framework VARCHAR(100),
    runtime_version VARCHAR(50),
    build_tool VARCHAR(50),
    port INT,
    dockerfile_path VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_analysis_jobs_deployment_id ON analysis_jobs(deployment_id);
