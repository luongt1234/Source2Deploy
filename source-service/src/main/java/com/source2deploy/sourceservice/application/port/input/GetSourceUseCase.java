package com.source2deploy.sourceservice.application.port.input;

import com.source2deploy.sourceservice.application.dto.SourceDto;

import java.util.List;
import java.util.UUID;

public interface GetSourceUseCase {
    SourceDto getSourceById(UUID id);
    List<SourceDto> getSourcesByProjectId(UUID projectId);
}
