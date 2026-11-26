package com.source2deploy.projectservice.application.port.output;

import com.source2deploy.projectservice.domain.model.Project;

public interface ProjectEventPublisherPort {
    void publishProjectCreated(Project project);
}
