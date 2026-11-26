package com.source2deploy.analyzerservice.presentation.controller;

import com.source2deploy.analyzerservice.application.service.AnalysisApplicationService;
import com.source2deploy.analyzerservice.domain.model.AnalysisJob;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analyzer/jobs")
@Tag(name = "Analyzer Jobs", description = "Endpoints for checking analysis job results")
public class AnalysisJobController {

    private final AnalysisApplicationService service;

    public AnalysisJobController(AnalysisApplicationService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get analysis job by ID")
    public ResponseEntity<AnalysisJob> getJob(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getJobById(id));
    }

    @PostMapping("/trigger")
    @Operation(summary = "Manually trigger repository analysis")
    public ResponseEntity<AnalysisJob> triggerAnalysis(@RequestBody Map<String, String> request) {
        UUID deploymentId = UUID.fromString(request.get("deploymentId"));
        UUID projectId = UUID.fromString(request.get("projectId"));
        String repoUrl = request.get("repositoryUrl");

        AnalysisJob job = service.analyze(deploymentId, projectId, repoUrl);
        return ResponseEntity.ok(job);
    }
}
