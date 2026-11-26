package com.source2deploy.auth.application.port;

import java.time.Instant;
import java.util.UUID;

public interface AccountTokens {
    enum Purpose { VERIFY_EMAIL, RESET_PASSWORD }

    boolean replace(String hash, UUID user, Purpose purpose, Instant expiresAt);
    boolean verify(String hash);
    boolean reset(String hash, String passwordHash);
}
