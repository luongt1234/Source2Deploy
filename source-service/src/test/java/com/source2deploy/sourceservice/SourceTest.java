package com.source2deploy.sourceservice;

import com.source2deploy.sourceservice.domain.model.SourceSnapshot;
import com.source2deploy.sourceservice.domain.model.SourceStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure domain unit test - executes without Spring context.
 */
class SourceTest {

    @Test
    void shouldCreateAndMarkSourceSnapshotReady() {
        UUID projectId = UUID.randomUUID();
        SourceSnapshot snapshot = SourceSnapshot.create(
                projectId,
                "https://github.com/myorg/myservice.git",
                "main",
                null
        );

        assertNotNull(snapshot.getId());
        assertEquals(projectId, snapshot.getProjectId());
        assertEquals(SourceStatus.PENDING, snapshot.getStatus());
        assertEquals("main", snapshot.getBranch());

        snapshot.startFetching();
        assertEquals(SourceStatus.FETCHING, snapshot.getStatus());

        snapshot.markReady("a1b2c3d", "/workspaces/123");
        assertEquals(SourceStatus.READY, snapshot.getStatus());
        assertEquals("a1b2c3d", snapshot.getCommitHash());
        assertEquals("/workspaces/123", snapshot.getStoragePath());
    }

    @Test
    void shouldThrowExceptionWhenInvalidGithubUrl() {
        assertThrows(IllegalArgumentException.class, () ->
                SourceSnapshot.create(UUID.randomUUID(), "http://invalidsite.com/repo", "main", null)
        );
    }
}
