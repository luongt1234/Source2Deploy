package com.source2deploy.projectservice.domain.repository;

import com.source2deploy.projectservice.domain.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Pure Domain Repository Interface - Defined in Domain layer.
 * Implemented by Infrastructure layer.
 */
public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(UUID id);

    List<Project> findAll();

    boolean existsByName(String name);

    void deleteById(UUID id);
}
