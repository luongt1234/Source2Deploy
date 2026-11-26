package com.source2deploy.sourceservice.application.port.output;

import com.source2deploy.sourceservice.domain.model.SourceSnapshot;

public interface SourceEventPublisherPort {
    void publishSourceReady(SourceSnapshot snapshot);
}
