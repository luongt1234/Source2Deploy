package com.source2deploy.auth.domain.model;

import java.util.Set;

public record Role(long id, String name, String description, Set<Permission> permissions) {
    public Role {
        permissions = Set.copyOf(permissions);
    }
}
