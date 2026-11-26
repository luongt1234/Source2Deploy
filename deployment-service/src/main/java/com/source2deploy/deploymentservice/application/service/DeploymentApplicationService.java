package com.source2deploy.deploymentservice.application.service;

import com.source2deploy.deploymentservice.application.command.CreateDeploymentCommand;
import com.source2deploy.deploymentservice.application.dto.DeploymentDto;
import com.source2deploy.deploymentservice.application.port.input.CancelDeploymentUseCase;
import com.source2deploy.deploymentservice.application.port.input.CreateDeploymentUseCase;
import com.source2deploy.deploymentservice.application.port.input.GetDeploymentUseCase;
import com.source2deploy.deploymentservice.application.port.output.DeploymentEventPublisherPort;
import com.source2deploy.deploymentservice.domain.exception.DeploymentNotFoundException;
import com.source2deploy.deploymentservice.domain.model.Deployment;
import com.source2deploy.deploymentservice.domain.repository.DeploymentRepository;

import java.util.List;
import java.util.UUID;

public class DeploymentApplicationService
        implements CreateDeploymentUseCase, GetDeploymentUseCase, CancelDeploymentUseCase {

    private final DeploymentRepository deploymentRepository;
    private final DeploymentEventPublisherPort eventPublisherPort;

    public DeploymentApplicationService(DeploymentRepository deploymentRepository,
                                       DeploymentEventPublisherPort eventPublisherPort) {
        this.deploymentRepository = deploymentRepository;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    public DeploymentDto createDeployment(CreateDeploymentCommand command) {
        Deployment deployment = Deployment.create(
                command.projectId(),
                command.repositoryUrl(),
                command.branch(),
                command.commitHash()
        );

        deployment.startAnalysis();
        Deployment saved = deploymentRepository.save(deployment);
        eventPublisherPort.publishDeploymentStarted(saved, command.environmentVariables());

        return DeploymentDto.fromDomain(saved);
    }

    @Override
    public DeploymentDto getDeploymentById(UUID id) {
        return deploymentRepository.findById(id)
                .map(DeploymentDto::fromDomain)
                .orElseThrow(() -> new DeploymentNotFoundException(id));
    }

    @Override
    public List<DeploymentDto> getDeploymentsByProjectId(UUID projectId) {
        return deploymentRepository.findByProjectId(projectId)
                .stream()
                .map(DeploymentDto::fromDomain)
                .toList();
    }

    @Override
    public DeploymentDto cancelDeployment(UUID id) {
        Deployment deployment = deploymentRepository.findById(id)
                .orElseThrow(() -> new DeploymentNotFoundException(id));

        deployment.cancel();
        Deployment updated = deploymentRepository.save(deployment);
        return DeploymentDto.fromDomain(updated);
    }
}
