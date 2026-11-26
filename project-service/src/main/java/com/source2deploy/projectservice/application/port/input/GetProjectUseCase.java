package com.source2deploy.projectservice.application.port.input;

import com.source2deploy.projectservice.application.dto.ProjectDto;

import java.util.List;
import java.util.UUID;

public interface GetProjectUseCase {
    ProjectDto getProjectById(UUID id);
    List<ProjectDto> getAllProjects();
}
