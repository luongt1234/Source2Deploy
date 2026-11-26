package com.source2deploy.auth.application.port;

public interface RecoveryRequests {
    void request(String email, AccountTokens.Purpose purpose);
}
