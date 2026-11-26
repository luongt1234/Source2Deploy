package com.source2deploy.analyzerservice.application.port.output;

import com.source2deploy.analyzerservice.domain.model.AnalysisJob;

public interface AnalysisEventPublisherPort {
    void publishAnalysisCompleted(AnalysisJob job);
    void publishAnalysisFailed(AnalysisJob job, String reason);
}
