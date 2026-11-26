package com.source2deploy.sourceservice.application.port.input;

import com.source2deploy.sourceservice.application.command.FetchSourceCommand;
import com.source2deploy.sourceservice.application.dto.SourceDto;

public interface FetchSourceUseCase {
    SourceDto fetchSource(FetchSourceCommand command);
}
