package com.source2deploy.event.analysis;

import java.time.Instant;
import java.util.UUID;

public record AnalysisCompletedEvent(
        UUID analysisJobId,
        UUID deploymentId,
        UUID projectId,
        String detectedLanguage,
        String detectedFramework,
        String runtimeVersion,
        String buildTool,
        int port,
        String dockerfilePath,
        Instant analyzedAt
) {}
