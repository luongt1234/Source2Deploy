package com.source2deploy.sourceservice.infrastructure.persistence;

import com.source2deploy.sourceservice.domain.model.SourceSnapshot;
import com.source2deploy.sourceservice.domain.repository.SourceRepository;
import com.source2deploy.sourceservice.infrastructure.persistence.entity.SourceJpaEntity;
import com.source2deploy.sourceservice.infrastructure.persistence.repository.SpringDataSourceRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SourcePersistenceAdapter implements SourceRepository {

    private final SpringDataSourceRepository springDataRepository;

    public SourcePersistenceAdapter(SpringDataSourceRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public SourceSnapshot save(SourceSnapshot snapshot) {
        SourceJpaEntity entity = toEntity(snapshot);
        SourceJpaEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<SourceSnapshot> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<SourceSnapshot> findByProjectId(UUID projectId) {
        return springDataRepository.findByProjectId(projectId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private SourceJpaEntity toEntity(SourceSnapshot domain) {
        return new SourceJpaEntity(
                domain.getId(),
                domain.getProjectId(),
                domain.getRepositoryUrl(),
                domain.getBranch(),
                domain.getCommitHash(),
                domain.getStatus(),
                domain.getStoragePath(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getVersion()
        );
    }

    private SourceSnapshot toDomain(SourceJpaEntity entity) {
        return new SourceSnapshot(
                entity.getId(),
                entity.getProjectId(),
                entity.getRepositoryUrl(),
                entity.getBranch(),
                entity.getCommitHash(),
                entity.getStatus(),
                entity.getStoragePath(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
