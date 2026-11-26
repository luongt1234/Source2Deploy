package com.source2deploy.projectservice.application.port.input;

import com.source2deploy.projectservice.application.command.CreateProjectCommand;
import com.source2deploy.projectservice.application.dto.ProjectDto;

public interface CreateProjectUseCase {
    ProjectDto createProject(CreateProjectCommand command);
}
