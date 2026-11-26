package com.source2deploy.infrastructureservice.presentation.controller;

import com.source2deploy.infrastructureservice.application.service.InfrastructureApplicationService;
import com.source2deploy.infrastructureservice.domain.model.InfrastructureDeployment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/infra/deployments")
@Tag(name = "Infrastructure Deployments", description = "Endpoints for managing provisioned cloud infrastructure")
public class InfrastructureController {

    private final InfrastructureApplicationService service;

    public InfrastructureController(InfrastructureApplicationService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get infrastructure deployment details")
    public ResponseEntity<InfrastructureDeployment> getInfra(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }
}
