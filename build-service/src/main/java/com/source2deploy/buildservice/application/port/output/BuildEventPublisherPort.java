package com.source2deploy.buildservice.application.port.output;

import com.source2deploy.buildservice.domain.model.BuildJob;

public interface BuildEventPublisherPort {
    void publishBuildCompleted(BuildJob job);
    void publishBuildFailed(BuildJob job, String reason);
}
