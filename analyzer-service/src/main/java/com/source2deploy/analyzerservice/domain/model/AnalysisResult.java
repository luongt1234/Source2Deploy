package com.source2deploy.analyzerservice.domain.model;

public record AnalysisResult(
        String detectedLanguage,
        String detectedFramework,
        String runtimeVersion,
        String buildTool,
        int port,
        String dockerfilePath
) {}
