package com.source2deploy.analyzerservice.infrastructure.persistence.repository;

import com.source2deploy.analyzerservice.infrastructure.persistence.entity.AnalysisJobJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataAnalysisJobRepository extends JpaRepository<AnalysisJobJpaEntity, UUID> {
    Optional<AnalysisJobJpaEntity> findByDeploymentId(UUID deploymentId);
}
