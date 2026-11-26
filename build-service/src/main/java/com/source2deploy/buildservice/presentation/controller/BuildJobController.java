package com.source2deploy.buildservice.presentation.controller;

import com.source2deploy.buildservice.application.service.BuildApplicationService;
import com.source2deploy.buildservice.domain.model.BuildJob;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/builds")
@Tag(name = "Builds", description = "Endpoints for build jobs")
public class BuildJobController {

    private final BuildApplicationService service;

    public BuildJobController(BuildApplicationService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get build job details")
    public ResponseEntity<BuildJob> getBuild(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getJobById(id));
    }
}
