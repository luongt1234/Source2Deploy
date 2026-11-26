package com.source2deploy.auth.domain.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record User(UUID id, String username, String email, String passwordHash, UUID credentialVersion, boolean enabled,
                   boolean emailVerified, Instant createdAt, Instant updatedAt, Set<Role> roles) {
    public User {
        roles = Set.copyOf(roles);
    }

    @Override
    public String toString() {
        return "User[id=" + id + "]";
    }
}
