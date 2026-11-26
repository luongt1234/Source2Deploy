package com.source2deploy.sourceservice.presentation.advice;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        List<String> details
) {
    public static ErrorResponse of(int status, String code, String message, String path, List<String> details) {
        return new ErrorResponse(Instant.now(), status, code, message, path, details != null ? details : List.of());
    }
}
