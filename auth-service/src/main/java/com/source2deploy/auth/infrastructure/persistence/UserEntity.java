package com.source2deploy.auth.infrastructure.persistence;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.source2deploy.auth.domain.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth_user")
class UserEntity {
    @Id UUID id;
    String username;
    String email;
    String passwordHash;
    UUID credentialVersion;
    boolean enabled;
    boolean emailVerified;
    Instant createdAt;
    Instant updatedAt;

    @ManyToMany
    @JoinTable(name = "auth_user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<RoleEntity> roles = new HashSet<>();

    protected UserEntity() {}

    UserEntity(User user, RoleEntity role) {
        this.id = user.id();
        this.username = user.username();
        this.email = user.email();
        this.passwordHash = user.passwordHash();
        this.credentialVersion = user.credentialVersion();
        this.enabled = user.enabled();
        this.emailVerified = user.emailVerified();
        this.createdAt = user.createdAt();
        this.updatedAt = user.updatedAt();
        this.roles.add(role);
    }
}
