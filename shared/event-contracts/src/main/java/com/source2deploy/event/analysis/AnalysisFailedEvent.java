package com.source2deploy.event.analysis;

import java.time.Instant;
import java.util.UUID;

public record AnalysisFailedEvent(
        UUID analysisJobId,
        UUID deploymentId,
        UUID projectId,
        String reason,
        Instant failedAt
) {}
