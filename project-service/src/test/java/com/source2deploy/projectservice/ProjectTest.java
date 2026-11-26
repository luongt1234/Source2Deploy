package com.source2deploy.projectservice;

import com.source2deploy.projectservice.domain.model.Project;
import com.source2deploy.projectservice.domain.model.ProjectStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure domain unit test - executes without Spring context.
 */
class ProjectTest {

    @Test
    void shouldCreateValidProject() {
        UUID id = UUID.randomUUID();
        Project project = Project.create(
                "My Service",
                "https://github.com/myorg/myservice.git",
                "main",
                null
        );

        assertNotNull(project.getId());
        assertEquals("My Service", project.getName());
        assertEquals(ProjectStatus.ACTIVE, project.getStatus());
        assertEquals("main", project.getDefaultBranch());
    }

    @Test
    void shouldThrowExceptionWhenInvalidGithubUrl() {
        assertThrows(IllegalArgumentException.class, () ->
                Project.create("Invalid", "http://invalidsite.com/repo", "main", null)
        );
    }
}
