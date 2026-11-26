package com.source2deploy.projectservice.presentation.controller;

import com.source2deploy.projectservice.application.command.CreateProjectCommand;
import com.source2deploy.projectservice.application.dto.ProjectDto;
import com.source2deploy.projectservice.application.port.input.CreateProjectUseCase;
import com.source2deploy.projectservice.application.port.input.GetProjectUseCase;
import com.source2deploy.projectservice.presentation.request.CreateProjectRequest;
import com.source2deploy.projectservice.presentation.response.ApiResponse;
import com.source2deploy.projectservice.presentation.response.ProjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Endpoints for managing Source2Deploy projects")
public class ProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final GetProjectUseCase getProjectUseCase;

    public ProjectController(CreateProjectUseCase createProjectUseCase, GetProjectUseCase getProjectUseCase) {
        this.createProjectUseCase = createProjectUseCase;
        this.getProjectUseCase = getProjectUseCase;
    }

    @PostMapping
    @Operation(summary = "Create a new project", description = "Registers a new GitHub repository project in the platform")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request) {

        CreateProjectCommand command = new CreateProjectCommand(
                request.name(),
                request.repositoryUrl(),
                request.defaultBranch(),
                request.githubConnectionId()
        );

        ProjectDto created = createProjectUseCase.createProject(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(ProjectResponse.fromDto(created), "Project created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(@PathVariable UUID id) {
        ProjectDto dto = getProjectUseCase.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.ok(ProjectResponse.fromDto(dto)));
    }

    @GetMapping
    @Operation(summary = "List all projects")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAllProjects() {
        List<ProjectResponse> projects = getProjectUseCase.getAllProjects()
                .stream()
                .map(ProjectResponse::fromDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(projects));
    }
}
