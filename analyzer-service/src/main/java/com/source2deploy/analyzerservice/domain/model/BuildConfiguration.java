package com.source2deploy.analyzerservice.domain.model;

import java.util.Map;

public record BuildConfiguration(
        String baseImage,
        String buildCommand,
        String runCommand,
        int targetPort,
        Map<String, String> buildArgs
) {}
