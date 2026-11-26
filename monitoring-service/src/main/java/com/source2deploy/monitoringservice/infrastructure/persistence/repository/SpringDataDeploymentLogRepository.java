package com.source2deploy.monitoringservice.infrastructure.persistence.repository;

import com.source2deploy.monitoringservice.infrastructure.persistence.entity.DeploymentLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataDeploymentLogRepository extends JpaRepository<DeploymentLogJpaEntity, UUID> {
    List<DeploymentLogJpaEntity> findByDeploymentIdOrderByCreatedAtAsc(UUID deploymentId);
}
