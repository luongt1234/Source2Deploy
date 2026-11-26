package com.source2deploy.sourceservice.presentation.controller;

import com.source2deploy.sourceservice.application.command.FetchSourceCommand;
import com.source2deploy.sourceservice.application.dto.SourceDto;
import com.source2deploy.sourceservice.application.port.input.FetchSourceUseCase;
import com.source2deploy.sourceservice.application.port.input.GetSourceUseCase;
import com.source2deploy.sourceservice.presentation.request.FetchSourceRequest;
import com.source2deploy.sourceservice.presentation.response.ApiResponse;
import com.source2deploy.sourceservice.presentation.response.SourceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sources")
@Tag(name = "Sources", description = "Endpoints for fetching and managing source code snapshots")
public class SourceController {

    private final FetchSourceUseCase fetchSourceUseCase;
    private final GetSourceUseCase getSourceUseCase;

    public SourceController(FetchSourceUseCase fetchSourceUseCase, GetSourceUseCase getSourceUseCase) {
        this.fetchSourceUseCase = fetchSourceUseCase;
        this.getSourceUseCase = getSourceUseCase;
    }

    @PostMapping("/fetch")
    @Operation(summary = "Fetch source code snapshot", description = "Clones or snapshots a GitHub repository branch/commit")
    public ResponseEntity<ApiResponse<SourceResponse>> fetchSource(
            @Valid @RequestBody FetchSourceRequest request) {

        FetchSourceCommand command = new FetchSourceCommand(
                request.projectId(),
                request.repositoryUrl(),
                request.branch(),
                request.commitHash()
        );

        SourceDto fetched = fetchSourceUseCase.fetchSource(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(SourceResponse.fromDto(fetched), "Source snapshot fetched successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get source snapshot by ID")
    public ResponseEntity<ApiResponse<SourceResponse>> getSourceById(@PathVariable UUID id) {
        SourceDto dto = getSourceUseCase.getSourceById(id);
        return ResponseEntity.ok(ApiResponse.ok(SourceResponse.fromDto(dto)));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "List source snapshots by project ID")
    public ResponseEntity<ApiResponse<List<SourceResponse>>> getSourcesByProject(@PathVariable UUID projectId) {
        List<SourceResponse> sources = getSourceUseCase.getSourcesByProjectId(projectId)
                .stream()
                .map(SourceResponse::fromDto)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(sources));
    }
}
