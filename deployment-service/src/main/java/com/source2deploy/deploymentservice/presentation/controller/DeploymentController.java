package com.source2deploy.deploymentservice.presentation.controller;

import com.source2deploy.deploymentservice.application.command.CreateDeploymentCommand;
import com.source2deploy.deploymentservice.application.dto.DeploymentDto;
import com.source2deploy.deploymentservice.application.port.input.CancelDeploymentUseCase;
import com.source2deploy.deploymentservice.application.port.input.CreateDeploymentUseCase;
import com.source2deploy.deploymentservice.application.port.input.GetDeploymentUseCase;
import com.source2deploy.deploymentservice.presentation.request.CreateDeploymentRequest;
import com.source2deploy.deploymentservice.presentation.response.ApiResponse;
import com.source2deploy.deploymentservice.presentation.response.DeploymentResponse;
import com.source2deploy.deploymentservice.presentation.response.DeploymentStageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deployments")
@Tag(name = "Deployments", description = "Endpoints for managing application deployments")
public class DeploymentController {

    private final CreateDeploymentUseCase createDeploymentUseCase;
    private final GetDeploymentUseCase getDeploymentUseCase;
    private final CancelDeploymentUseCase cancelDeploymentUseCase;

    public DeploymentController(CreateDeploymentUseCase createDeploymentUseCase,
                                GetDeploymentUseCase getDeploymentUseCase,
                                CancelDeploymentUseCase cancelDeploymentUseCase) {
        this.createDeploymentUseCase = createDeploymentUseCase;
        this.getDeploymentUseCase = getDeploymentUseCase;
        this.cancelDeploymentUseCase = cancelDeploymentUseCase;
    }

    @PostMapping
    @Operation(summary = "Create deployment", description = "Initiates a new automated deployment workflow")
    public ResponseEntity<ApiResponse<DeploymentResponse>> createDeployment(
            @Valid @RequestBody CreateDeploymentRequest request) {

        CreateDeploymentCommand command = new CreateDeploymentCommand(
                request.projectId(),
                request.repositoryUrl(),
                request.branch(),
                request.commitHash(),
                request.environmentVariables()
        );

        DeploymentDto dto = createDeploymentUseCase.createDeployment(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(DeploymentResponse.fromDto(dto), "Deployment initiated"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get deployment by ID")
    public ResponseEntity<ApiResponse<DeploymentResponse>> getDeploymentById(@PathVariable UUID id) {
        DeploymentDto dto = getDeploymentUseCase.getDeploymentById(id);
        return ResponseEntity.ok(ApiResponse.ok(DeploymentResponse.fromDto(dto)));
    }

    @GetMapping("/{id}/stages")
    @Operation(summary = "Get deployment stages")
    public ResponseEntity<ApiResponse<List<DeploymentStageResponse>>> getDeploymentStages(@PathVariable UUID id) {
        DeploymentDto dto = getDeploymentUseCase.getDeploymentById(id);
        List<DeploymentStageResponse> stages = dto.stages().stream()
                .map(DeploymentStageResponse::fromDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(stages));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a running deployment")
    public ResponseEntity<ApiResponse<DeploymentResponse>> cancelDeployment(@PathVariable UUID id) {
        DeploymentDto dto = cancelDeploymentUseCase.cancelDeployment(id);
        return ResponseEntity.ok(ApiResponse.ok(DeploymentResponse.fromDto(dto), "Deployment cancelled"));
    }
}
