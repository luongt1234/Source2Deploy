package com.source2deploy.projectservice.infrastructure.persistence.repository;

import com.source2deploy.projectservice.infrastructure.persistence.entity.ProjectJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataProjectRepository extends JpaRepository<ProjectJpaEntity, UUID> {
    boolean existsByName(String name);
}
