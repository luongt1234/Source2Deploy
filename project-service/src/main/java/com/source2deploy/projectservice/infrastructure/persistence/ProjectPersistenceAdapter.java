package com.source2deploy.projectservice.infrastructure.persistence;

import com.source2deploy.projectservice.domain.model.Project;
import com.source2deploy.projectservice.domain.repository.ProjectRepository;
import com.source2deploy.projectservice.infrastructure.persistence.entity.ProjectJpaEntity;
import com.source2deploy.projectservice.infrastructure.persistence.repository.SpringDataProjectRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProjectPersistenceAdapter implements ProjectRepository {

    private final SpringDataProjectRepository springDataRepository;

    public ProjectPersistenceAdapter(SpringDataProjectRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Project save(Project project) {
        ProjectJpaEntity entity = toEntity(project);
        ProjectJpaEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Project> findAll() {
        return springDataRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByName(String name) {
        return springDataRepository.existsByName(name);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
    }

    private ProjectJpaEntity toEntity(Project domain) {
        return new ProjectJpaEntity(
                domain.getId(),
                domain.getName(),
                domain.getRepositoryUrl(),
                domain.getDefaultBranch(),
                domain.getStatus(),
                domain.getGithubConnectionId(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getVersion()
        );
    }

    private Project toDomain(ProjectJpaEntity entity) {
        return new Project(
                entity.getId(),
                entity.getName(),
                entity.getRepositoryUrl(),
                entity.getDefaultBranch(),
                entity.getStatus(),
                entity.getGithubConnectionId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
