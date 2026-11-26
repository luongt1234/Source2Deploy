package com.source2deploy.analyzerservice;

import com.source2deploy.analyzerservice.domain.model.AnalysisJob;
import com.source2deploy.analyzerservice.domain.model.AnalysisResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisTest {

    @Test
    void shouldCompleteAnalysisJobWithResult() {
        UUID deploymentId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        AnalysisJob job = AnalysisJob.create(deploymentId, projectId);
        assertEquals("ANALYZING", job.getStatus());

        AnalysisResult result = new AnalysisResult("Java", "Spring Boot", "21", "maven", 8080, "Dockerfile");
        job.complete(result);

        assertEquals("COMPLETED", job.getStatus());
        assertEquals("Java", job.getResult().detectedLanguage());
        assertEquals(8080, job.getResult().port());
    }
}
