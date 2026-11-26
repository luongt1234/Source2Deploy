package com.source2deploy.deployservice.presentation.controller;

import com.source2deploy.deployservice.application.service.DeployApplicationService;
import com.source2deploy.deployservice.domain.model.ApplicationRelease;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deploy/releases")
@Tag(name = "Releases", description = "Endpoints for managing ECS application releases")
public class ReleaseController {

    private final DeployApplicationService service;

    public ReleaseController(DeployApplicationService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application release details")
    public ResponseEntity<ApplicationRelease> getRelease(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }
}
