package com.source2deploy.registryservice.presentation.controller;

import com.source2deploy.registryservice.application.service.RegistryApplicationService;
import com.source2deploy.registryservice.domain.model.ContainerImage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/registry/images")
@Tag(name = "Container Images", description = "Endpoints for container image metadata")
public class RegistryController {

    private final RegistryApplicationService service;

    public RegistryController(RegistryApplicationService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get container image details")
    public ResponseEntity<ContainerImage> getImage(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getImageById(id));
    }
}
