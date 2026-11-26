package com.source2deploy.buildservice.infrastructure.persistence.repository;

import com.source2deploy.buildservice.infrastructure.persistence.entity.BuildJobJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataBuildJobRepository extends JpaRepository<BuildJobJpaEntity, UUID> {
    Optional<BuildJobJpaEntity> findByDeploymentId(UUID deploymentId);
}
