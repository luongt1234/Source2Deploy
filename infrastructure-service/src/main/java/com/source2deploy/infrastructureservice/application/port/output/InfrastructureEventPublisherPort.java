package com.source2deploy.infrastructureservice.application.port.output;

import com.source2deploy.infrastructureservice.domain.model.InfrastructureDeployment;

public interface InfrastructureEventPublisherPort {
    void publishInfrastructureReady(InfrastructureDeployment deployment);
    void publishInfrastructureFailed(InfrastructureDeployment deployment, String reason);
}
