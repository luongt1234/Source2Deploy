package com.source2deploy.sourceservice.application.service;

import com.source2deploy.sourceservice.application.command.FetchSourceCommand;
import com.source2deploy.sourceservice.application.dto.SourceDto;
import com.source2deploy.sourceservice.application.port.input.FetchSourceUseCase;
import com.source2deploy.sourceservice.application.port.input.GetSourceUseCase;
import com.source2deploy.sourceservice.application.port.output.SourceEventPublisherPort;
import com.source2deploy.sourceservice.domain.exception.SourceNotFoundException;
import com.source2deploy.sourceservice.domain.model.SourceSnapshot;
import com.source2deploy.sourceservice.domain.repository.SourceRepository;

import java.util.List;
import java.util.UUID;

/**
 * Pure Application Service - Coordinates domain operations and output ports.
 * Does not depend on Spring or external frameworks.
 */
public class SourceApplicationService implements FetchSourceUseCase, GetSourceUseCase {

    private final SourceRepository sourceRepository;
    private final SourceEventPublisherPort eventPublisherPort;

    public SourceApplicationService(SourceRepository sourceRepository,
                                    SourceEventPublisherPort eventPublisherPort) {
        this.sourceRepository = sourceRepository;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    public SourceDto fetchSource(FetchSourceCommand command) {
        SourceSnapshot snapshot = SourceSnapshot.create(
                command.projectId(),
                command.repositoryUrl(),
                command.branch(),
                command.commitHash()
        );

        snapshot.startFetching();

        // Foundation: simulate workspace snapshot path
        String resolvedCommit = (command.commitHash() != null && !command.commitHash().isBlank())
                ? command.commitHash()
                : UUID.randomUUID().toString().substring(0, 7);
        String storagePath = "/workspaces/" + command.projectId() + "/" + snapshot.getId();

        snapshot.markReady(resolvedCommit, storagePath);

        SourceSnapshot saved = sourceRepository.save(snapshot);
        eventPublisherPort.publishSourceReady(saved);

        return SourceDto.fromDomain(saved);
    }

    @Override
    public SourceDto getSourceById(UUID id) {
        return sourceRepository.findById(id)
                .map(SourceDto::fromDomain)
                .orElseThrow(() -> new SourceNotFoundException(id));
    }

    @Override
    public List<SourceDto> getSourcesByProjectId(UUID projectId) {
        return sourceRepository.findByProjectId(projectId)
                .stream()
                .map(SourceDto::fromDomain)
                .toList();
    }
}
