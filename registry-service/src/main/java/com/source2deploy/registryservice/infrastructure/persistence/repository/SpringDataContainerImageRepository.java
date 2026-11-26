package com.source2deploy.registryservice.infrastructure.persistence.repository;

import com.source2deploy.registryservice.infrastructure.persistence.entity.ContainerImageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataContainerImageRepository extends JpaRepository<ContainerImageJpaEntity, UUID> {
    Optional<ContainerImageJpaEntity> findByDeploymentId(UUID deploymentId);
}
