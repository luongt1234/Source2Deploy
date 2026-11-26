package com.source2deploy.sourceservice.infrastructure.persistence.repository;

import com.source2deploy.sourceservice.infrastructure.persistence.entity.SourceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataSourceRepository extends JpaRepository<SourceJpaEntity, UUID> {
    List<SourceJpaEntity> findByProjectId(UUID projectId);
}
