package com.source2deploy.sourceservice.domain.exception;

import java.util.UUID;

public class SourceNotFoundException extends RuntimeException {
    public SourceNotFoundException(UUID id) {
        super("Source snapshot not found with id: " + id);
    }
}
