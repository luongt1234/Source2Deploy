package com.source2deploy.deploymentservice.infrastructure.persistence;

import com.source2deploy.deploymentservice.domain.model.Deployment;
import com.source2deploy.deploymentservice.domain.model.DeploymentStage;
import com.source2deploy.deploymentservice.domain.repository.DeploymentRepository;
import com.source2deploy.deploymentservice.infrastructure.persistence.entity.DeploymentJpaEntity;
import com.source2deploy.deploymentservice.infrastructure.persistence.entity.DeploymentStageJpaEntity;
import com.source2deploy.deploymentservice.infrastructure.persistence.repository.SpringDataDeploymentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DeploymentPersistenceAdapter implements DeploymentRepository {

    private final SpringDataDeploymentRepository springDataRepository;

    public DeploymentPersistenceAdapter(SpringDataDeploymentRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Deployment save(Deployment domain) {
        DeploymentJpaEntity entity = new DeploymentJpaEntity(
                domain.getId(),
                domain.getProjectId(),
                domain.getRepositoryUrl(),
                domain.getBranch(),
                domain.getCommitHash(),
                domain.getStatus(),
                domain.getLiveUrl(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getVersion()
        );

        for (DeploymentStage stage : domain.getStages()) {
            DeploymentStageJpaEntity stageEntity = new DeploymentStageJpaEntity(
                    stage.id(),
                    entity,
                    stage.stageName(),
                    stage.status(),
                    stage.startedAt(),
                    stage.completedAt(),
                    stage.message()
            );
            entity.getStages().add(stageEntity);
        }

        DeploymentJpaEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Deployment> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Deployment> findByProjectId(UUID projectId) {
        return springDataRepository.findByProjectId(projectId).stream().map(this::toDomain).toList();
    }

    private Deployment toDomain(DeploymentJpaEntity entity) {
        List<DeploymentStage> stages = entity.getStages().stream()
                .map(s -> new DeploymentStage(
                        s.getId(),
                        s.getStageName(),
                        s.getStatus(),
                        s.getStartedAt(),
                        s.getCompletedAt(),
                        s.getMessage()
                ))
                .toList();

        return new Deployment(
                entity.getId(),
                entity.getProjectId(),
                entity.getRepositoryUrl(),
                entity.getBranch(),
                entity.getCommitHash(),
                entity.getStatus(),
                entity.getLiveUrl(),
                stages,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
