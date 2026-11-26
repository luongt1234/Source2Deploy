CREATE TABLE IF NOT EXISTS application_releases (
    id UUID PRIMARY KEY,
    deployment_id UUID NOT NULL,
    project_id UUID NOT NULL,
    image_url VARCHAR(512) NOT NULL,
    ecs_cluster_arn VARCHAR(512),
    target_group_arn VARCHAR(512),
    release_status VARCHAR(50) NOT NULL,
    live_url VARCHAR(512),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_app_releases_deployment_id ON application_releases(deployment_id);
CREATE INDEX IF NOT EXISTS idx_app_releases_status ON application_releases(release_status);
