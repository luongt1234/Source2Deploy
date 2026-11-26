package com.source2deploy.monitoringservice.infrastructure.persistence;

import com.source2deploy.monitoringservice.domain.model.DeploymentLog;
import com.source2deploy.monitoringservice.domain.repository.DeploymentLogRepository;
import com.source2deploy.monitoringservice.infrastructure.persistence.entity.DeploymentLogJpaEntity;
import com.source2deploy.monitoringservice.infrastructure.persistence.repository.SpringDataDeploymentLogRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DeploymentLogPersistenceAdapter implements DeploymentLogRepository {

    private final SpringDataDeploymentLogRepository springDataRepository;

    public DeploymentLogPersistenceAdapter(SpringDataDeploymentLogRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public DeploymentLog save(DeploymentLog domain) {
        DeploymentLogJpaEntity entity = new DeploymentLogJpaEntity(
                domain.getId(),
                domain.getDeploymentId(),
                domain.getSourceService(),
                domain.getLogLevel(),
                domain.getMessage(),
                domain.getCreatedAt()
        );

        DeploymentLogJpaEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<DeploymentLog> findByDeploymentId(UUID deploymentId) {
        return springDataRepository.findByDeploymentIdOrderByCreatedAtAsc(deploymentId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private DeploymentLog toDomain(DeploymentLogJpaEntity entity) {
        return new DeploymentLog(
                entity.getId(),
                entity.getDeploymentId(),
                entity.getSourceService(),
                entity.getLogLevel(),
                entity.getMessage(),
                entity.getCreatedAt()
        );
    }
}
