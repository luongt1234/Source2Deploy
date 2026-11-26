CREATE TABLE IF NOT EXISTS container_images (
    id UUID PRIMARY KEY,
    deployment_id UUID NOT NULL,
    project_id UUID NOT NULL,
    repository_name VARCHAR(255) NOT NULL,
    image_tag VARCHAR(100) NOT NULL,
    image_digest VARCHAR(128),
    full_image_url VARCHAR(512) NOT NULL,
    size_bytes BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_container_images_deployment_id ON container_images(deployment_id);
CREATE INDEX IF NOT EXISTS idx_container_images_tag ON container_images(image_tag);
