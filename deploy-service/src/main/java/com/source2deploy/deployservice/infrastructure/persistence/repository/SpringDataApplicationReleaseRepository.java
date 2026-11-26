package com.source2deploy.deployservice.infrastructure.persistence.repository;

import com.source2deploy.deployservice.infrastructure.persistence.entity.ApplicationReleaseJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataApplicationReleaseRepository extends JpaRepository<ApplicationReleaseJpaEntity, UUID> {
    Optional<ApplicationReleaseJpaEntity> findByDeploymentId(UUID deploymentId);
}
