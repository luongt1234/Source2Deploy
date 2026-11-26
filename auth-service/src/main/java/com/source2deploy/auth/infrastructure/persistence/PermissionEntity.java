package com.source2deploy.auth.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth_permission")
class PermissionEntity {
    @Id long id;
    String name;
    String description;

    protected PermissionEntity() {}
}
