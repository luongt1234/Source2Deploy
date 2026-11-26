package com.source2deploy.infrastructureservice.infrastructure.persistence;

import com.source2deploy.infrastructureservice.domain.model.InfrastructureDeployment;
import com.source2deploy.infrastructureservice.domain.repository.InfrastructureDeploymentRepository;
import com.source2deploy.infrastructureservice.infrastructure.persistence.entity.InfrastructureDeploymentJpaEntity;
import com.source2deploy.infrastructureservice.infrastructure.persistence.repository.SpringDataInfrastructureDeploymentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class InfrastructureDeploymentPersistenceAdapter implements InfrastructureDeploymentRepository {

    private final SpringDataInfrastructureDeploymentRepository springDataRepository;

    public InfrastructureDeploymentPersistenceAdapter(SpringDataInfrastructureDeploymentRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public InfrastructureDeployment save(InfrastructureDeployment domain) {
        InfrastructureDeploymentJpaEntity entity = new InfrastructureDeploymentJpaEntity(
                domain.getId(),
                domain.getDeploymentId(),
                domain.getProjectId(),
                domain.getEnvironment(),
                domain.getStatus(),
                domain.getEcsClusterArn(),
                domain.getTargetGroupArn(),
                domain.getAlbDnsName(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getVersion()
        );

        InfrastructureDeploymentJpaEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<InfrastructureDeployment> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<InfrastructureDeployment> findByDeploymentId(UUID deploymentId) {
        return springDataRepository.findByDeploymentId(deploymentId).map(this::toDomain);
    }

    private InfrastructureDeployment toDomain(InfrastructureDeploymentJpaEntity entity) {
        return new InfrastructureDeployment(
                entity.getId(),
                entity.getDeploymentId(),
                entity.getProjectId(),
                entity.getEnvironment(),
                entity.getStatus(),
                entity.getEcsClusterArn(),
                entity.getTargetGroupArn(),
                entity.getAlbDnsName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
