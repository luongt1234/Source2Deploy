package com.source2deploy.projectservice.application.service;

import com.source2deploy.projectservice.application.command.CreateProjectCommand;
import com.source2deploy.projectservice.application.dto.ProjectDto;
import com.source2deploy.projectservice.application.port.input.CreateProjectUseCase;
import com.source2deploy.projectservice.application.port.input.GetProjectUseCase;
import com.source2deploy.projectservice.application.port.output.ProjectEventPublisherPort;
import com.source2deploy.projectservice.domain.exception.ProjectNotFoundException;
import com.source2deploy.projectservice.domain.model.Project;
import com.source2deploy.projectservice.domain.repository.ProjectRepository;

import java.util.List;
import java.util.UUID;

/**
 * Pure Application Service - Coordinates domain operations and output ports.
 * Does not depend on Spring or external frameworks.
 */
public class ProjectApplicationService implements CreateProjectUseCase, GetProjectUseCase {

    private final ProjectRepository projectRepository;
    private final ProjectEventPublisherPort eventPublisherPort;

    public ProjectApplicationService(ProjectRepository projectRepository,
                                     ProjectEventPublisherPort eventPublisherPort) {
        this.projectRepository = projectRepository;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    public ProjectDto createProject(CreateProjectCommand command) {
        if (projectRepository.existsByName(command.name())) {
            throw new IllegalArgumentException("Project name already exists: " + command.name());
        }

        Project project = Project.create(
                command.name(),
                command.repositoryUrl(),
                command.defaultBranch(),
                command.githubConnectionId()
        );

        Project savedProject = projectRepository.save(project);
        eventPublisherPort.publishProjectCreated(savedProject);

        return ProjectDto.fromDomain(savedProject);
    }

    @Override
    public ProjectDto getProjectById(UUID id) {
        return projectRepository.findById(id)
                .map(ProjectDto::fromDomain)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @Override
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectDto::fromDomain)
                .toList();
    }
}
