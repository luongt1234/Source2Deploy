package com.source2deploy.buildservice.infrastructure.persistence;

import com.source2deploy.buildservice.domain.model.BuildJob;
import com.source2deploy.buildservice.domain.repository.BuildJobRepository;
import com.source2deploy.buildservice.infrastructure.persistence.entity.BuildJobJpaEntity;
import com.source2deploy.buildservice.infrastructure.persistence.repository.SpringDataBuildJobRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class BuildJobPersistenceAdapter implements BuildJobRepository {

    private final SpringDataBuildJobRepository springDataRepository;

    public BuildJobPersistenceAdapter(SpringDataBuildJobRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public BuildJob save(BuildJob domain) {
        BuildJobJpaEntity entity = new BuildJobJpaEntity(
                domain.getId(),
                domain.getDeploymentId(),
                domain.getProjectId(),
                domain.getStatus(),
                domain.getImageTag(),
                domain.getBuildDurationMs(),
                domain.getBuildLogs(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getVersion()
        );

        BuildJobJpaEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<BuildJob> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<BuildJob> findByDeploymentId(UUID deploymentId) {
        return springDataRepository.findByDeploymentId(deploymentId).map(this::toDomain);
    }

    private BuildJob toDomain(BuildJobJpaEntity entity) {
        return new BuildJob(
                entity.getId(),
                entity.getDeploymentId(),
                entity.getProjectId(),
                entity.getStatus(),
                entity.getImageTag(),
                entity.getBuildDurationMs(),
                entity.getBuildLogs(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
