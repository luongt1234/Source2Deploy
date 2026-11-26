package com.source2deploy.monitoringservice.presentation.controller;

import com.source2deploy.monitoringservice.application.service.MonitoringApplicationService;
import com.source2deploy.monitoringservice.domain.model.DeploymentLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/monitoring/logs")
@Tag(name = "Monitoring Logs", description = "Endpoints for retrieving deployment audit and progress logs")
public class MonitoringController {

    private final MonitoringApplicationService service;

    public MonitoringController(MonitoringApplicationService service) {
        this.service = service;
    }

    @GetMapping("/{deploymentId}")
    @Operation(summary = "Get logs for a specific deployment")
    public ResponseEntity<List<DeploymentLog>> getLogs(@PathVariable UUID deploymentId) {
        return ResponseEntity.ok(service.getLogs(deploymentId));
    }
}
