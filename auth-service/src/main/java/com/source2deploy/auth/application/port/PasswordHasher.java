package com.source2deploy.auth.application.port;

public interface PasswordHasher {
    String hash(String password);
}
