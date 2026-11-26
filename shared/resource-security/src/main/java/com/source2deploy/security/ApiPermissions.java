package com.source2deploy.security;

import java.util.List;

public final class ApiPermissions {
    private ApiPermissions() {}

    public record Rule(String method, String path, String permission) {}

    public static final List<Rule> RULES = List.of(
            new Rule("GET", "/api/v1/users/me", "identity:read"),
            new Rule("GET", "/api/v1/users/me/permissions", "identity:read"),
            new Rule("POST", "/api/v1/projects", "project:create"),
            new Rule("GET", "/api/v1/projects", "project:read"),
            new Rule("GET", "/api/v1/projects/*", "project:read"),
            new Rule("POST", "/api/v1/deployments", "deployment:create"),
            new Rule("GET", "/api/v1/deployments/*", "deployment:read"),
            new Rule("GET", "/api/v1/deployments/*/stages", "deployment:read"),
            new Rule("POST", "/api/v1/deployments/*/cancel", "deployment:cancel"),
            new Rule("GET", "/api/v1/analyzer/jobs/*", "source:read"),
            new Rule("POST", "/api/v1/analyzer/jobs/trigger", "source:clone"),
            new Rule("GET", "/api/v1/builds/*", "deployment:read"),
            new Rule("GET", "/api/v1/registry/images/*", "deployment:read"),
            new Rule("GET", "/api/v1/infra/deployments/*", "deployment:read"),
            new Rule("GET", "/api/v1/deploy/releases/*", "deployment:read"),
            new Rule("GET", "/api/v1/monitoring/logs/*", "deployment:read"),
            new Rule("GET", "/ws/builds/**", "deployment:read"),
            new Rule("GET", "/ws/monitoring/**", "deployment:read")
    );
}
