package com.source2deploy.registryservice.application.port.output;

import com.source2deploy.registryservice.domain.model.ContainerImage;

public interface RegistryEventPublisherPort {
    void publishImagePushed(ContainerImage image);
}
