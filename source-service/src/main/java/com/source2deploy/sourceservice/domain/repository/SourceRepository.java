package com.source2deploy.sourceservice.domain.repository;

import com.source2deploy.sourceservice.domain.model.SourceSnapshot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Pure Domain Repository Interface - Defined in Domain layer.
 */
public interface SourceRepository {
    SourceSnapshot save(SourceSnapshot snapshot);
    Optional<SourceSnapshot> findById(UUID id);
    List<SourceSnapshot> findByProjectId(UUID projectId);
}
