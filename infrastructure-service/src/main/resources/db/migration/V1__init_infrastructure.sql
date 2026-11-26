CREATE TABLE IF NOT EXISTS infrastructure_deployments (
    id UUID PRIMARY KEY,
    deployment_id UUID NOT NULL,
    project_id UUID NOT NULL,
    environment VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    ecs_cluster_arn VARCHAR(512),
    target_group_arn VARCHAR(512),
    alb_dns_name VARCHAR(512),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_infra_deployments_deployment_id ON infrastructure_deployments(deployment_id);
