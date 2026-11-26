package com.source2deploy.deploymentservice.infrastructure.persistence.repository;

import com.source2deploy.deploymentservice.infrastructure.persistence.entity.DeploymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataDeploymentRepository extends JpaRepository<DeploymentJpaEntity, UUID> {
    List<DeploymentJpaEntity> findByProjectId(UUID projectId);
}
