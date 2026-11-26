package com.source2deploy.deployservice.infrastructure.persistence;

import com.source2deploy.deployservice.domain.model.ApplicationRelease;
import com.source2deploy.deployservice.domain.repository.ApplicationReleaseRepository;
import com.source2deploy.deployservice.infrastructure.persistence.entity.ApplicationReleaseJpaEntity;
import com.source2deploy.deployservice.infrastructure.persistence.repository.SpringDataApplicationReleaseRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ApplicationReleasePersistenceAdapter implements ApplicationReleaseRepository {

    private final SpringDataApplicationReleaseRepository springDataRepository;

    public ApplicationReleasePersistenceAdapter(SpringDataApplicationReleaseRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public ApplicationRelease save(ApplicationRelease domain) {
        ApplicationReleaseJpaEntity entity = new ApplicationReleaseJpaEntity(
                domain.getId(),
                domain.getDeploymentId(),
                domain.getProjectId(),
                domain.getImageUrl(),
                domain.getEcsClusterArn(),
                domain.getTargetGroupArn(),
                domain.getReleaseStatus(),
                domain.getLiveUrl(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getVersion()
        );

        ApplicationReleaseJpaEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ApplicationRelease> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ApplicationRelease> findByDeploymentId(UUID deploymentId) {
        return springDataRepository.findByDeploymentId(deploymentId).map(this::toDomain);
    }

    private ApplicationRelease toDomain(ApplicationReleaseJpaEntity entity) {
        return new ApplicationRelease(
                entity.getId(),
                entity.getDeploymentId(),
                entity.getProjectId(),
                entity.getImageUrl(),
                entity.getEcsClusterArn(),
                entity.getTargetGroupArn(),
                entity.getReleaseStatus(),
                entity.getLiveUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
