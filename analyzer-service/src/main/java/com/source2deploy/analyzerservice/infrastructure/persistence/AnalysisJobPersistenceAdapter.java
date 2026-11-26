package com.source2deploy.analyzerservice.infrastructure.persistence;

import com.source2deploy.analyzerservice.domain.model.AnalysisJob;
import com.source2deploy.analyzerservice.domain.model.AnalysisResult;
import com.source2deploy.analyzerservice.domain.repository.AnalysisJobRepository;
import com.source2deploy.analyzerservice.infrastructure.persistence.entity.AnalysisJobJpaEntity;
import com.source2deploy.analyzerservice.infrastructure.persistence.repository.SpringDataAnalysisJobRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AnalysisJobPersistenceAdapter implements AnalysisJobRepository {

    private final SpringDataAnalysisJobRepository springDataRepository;

    public AnalysisJobPersistenceAdapter(SpringDataAnalysisJobRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public AnalysisJob save(AnalysisJob domain) {
        AnalysisResult res = domain.getResult();
        AnalysisJobJpaEntity entity = new AnalysisJobJpaEntity(
                domain.getId(),
                domain.getDeploymentId(),
                domain.getProjectId(),
                domain.getStatus(),
                res != null ? res.detectedLanguage() : null,
                res != null ? res.detectedFramework() : null,
                res != null ? res.runtimeVersion() : null,
                res != null ? res.buildTool() : null,
                res != null ? res.port() : null,
                res != null ? res.dockerfilePath() : null,
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getVersion()
        );

        AnalysisJobJpaEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AnalysisJob> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<AnalysisJob> findByDeploymentId(UUID deploymentId) {
        return springDataRepository.findByDeploymentId(deploymentId).map(this::toDomain);
    }

    private AnalysisJob toDomain(AnalysisJobJpaEntity entity) {
        AnalysisResult res = null;
        if (entity.getDetectedLanguage() != null) {
            res = new AnalysisResult(
                    entity.getDetectedLanguage(),
                    entity.getDetectedFramework(),
                    entity.getRuntimeVersion(),
                    entity.getBuildTool(),
                    entity.getPort() != null ? entity.getPort() : 8080,
                    entity.getDockerfilePath()
            );
        }

        return new AnalysisJob(
                entity.getId(),
                entity.getDeploymentId(),
                entity.getProjectId(),
                entity.getStatus(),
                res,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
