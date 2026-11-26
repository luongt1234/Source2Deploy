package com.source2deploy.infrastructureservice.infrastructure.persistence.repository;

import com.source2deploy.infrastructureservice.infrastructure.persistence.entity.InfrastructureDeploymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataInfrastructureDeploymentRepository
        extends JpaRepository<InfrastructureDeploymentJpaEntity, UUID> {
    Optional<InfrastructureDeploymentJpaEntity> findByDeploymentId(UUID deploymentId);
}
