package com.source2deploy.auth.infrastructure.persistence;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth_role")
class RoleEntity {
    @Id long id;
    String name;
    String description;

    @ManyToMany
    @JoinTable(name = "auth_role_permission", joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    Set<PermissionEntity> permissions;

    protected RoleEntity() {}
}
