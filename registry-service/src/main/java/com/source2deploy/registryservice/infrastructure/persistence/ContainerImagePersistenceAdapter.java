package com.source2deploy.registryservice.infrastructure.persistence;

import com.source2deploy.registryservice.domain.model.ContainerImage;
import com.source2deploy.registryservice.domain.repository.ContainerImageRepository;
import com.source2deploy.registryservice.infrastructure.persistence.entity.ContainerImageJpaEntity;
import com.source2deploy.registryservice.infrastructure.persistence.repository.SpringDataContainerImageRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ContainerImagePersistenceAdapter implements ContainerImageRepository {

    private final SpringDataContainerImageRepository springDataRepository;

    public ContainerImagePersistenceAdapter(SpringDataContainerImageRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public ContainerImage save(ContainerImage domain) {
        ContainerImageJpaEntity entity = new ContainerImageJpaEntity(
                domain.getId(),
                domain.getDeploymentId(),
                domain.getProjectId(),
                domain.getRepositoryName(),
                domain.getImageTag(),
                domain.getImageDigest(),
                domain.getFullImageUrl(),
                domain.getSizeBytes(),
                domain.getCreatedAt(),
                domain.getVersion()
        );

        ContainerImageJpaEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ContainerImage> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ContainerImage> findByDeploymentId(UUID deploymentId) {
        return springDataRepository.findByDeploymentId(deploymentId).map(this::toDomain);
    }

    private ContainerImage toDomain(ContainerImageJpaEntity entity) {
        return new ContainerImage(
                entity.getId(),
                entity.getDeploymentId(),
                entity.getProjectId(),
                entity.getRepositoryName(),
                entity.getImageTag(),
                entity.getImageDigest(),
                entity.getFullImageUrl(),
                entity.getSizeBytes(),
                entity.getCreatedAt(),
                entity.getVersion()
        );
    }
}
