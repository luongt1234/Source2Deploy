package com.source2deploy.event;

/**
 * Centralized Kafka topic names across Source2Deploy microservices.
 */
public final class KafkaTopics {

    private KafkaTopics() {
        // Prevent instantiation
    }

    public static final String DEPLOYMENT_EVENTS = "deployment.events";
    public static final String ANALYSIS_EVENTS = "analysis.events";
    public static final String BUILD_EVENTS = "build.events";
    public static final String REGISTRY_EVENTS = "registry.events";
    public static final String INFRASTRUCTURE_EVENTS = "infrastructure.events";
    public static final String DEPLOY_EVENTS = "deploy.events";
    public static final String MONITORING_LOGS = "monitoring.logs";

    // Dead Letter Topics
    public static final String DEPLOYMENT_EVENTS_DLT = "deployment.events.DLT";
    public static final String ANALYSIS_EVENTS_DLT = "analysis.events.DLT";
    public static final String BUILD_EVENTS_DLT = "build.events.DLT";
    public static final String REGISTRY_EVENTS_DLT = "registry.events.DLT";
    public static final String INFRASTRUCTURE_EVENTS_DLT = "infrastructure.events.DLT";
    public static final String DEPLOY_EVENTS_DLT = "deploy.events.DLT";
}
