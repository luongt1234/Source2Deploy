package com.source2deploy.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.source2deploy.auth.application.port.UserRepository;
import com.source2deploy.auth.application.service.Accounts.DuplicateAccount;
import com.source2deploy.auth.domain.model.Permission;
import com.source2deploy.auth.domain.model.Role;
import com.source2deploy.auth.domain.model.User;
import jakarta.persistence.EntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
class JpaUsers implements UserRepository {
    private final SpringDataUsers users;
    private final EntityManager entities;

    JpaUsers(SpringDataUsers users, EntityManager entities) {
        this.users = users;
        this.entities = entities;
    }

    @Override
    @Transactional
    public User insert(User user) {
        RoleEntity role = entities.createQuery("select r from RoleEntity r where r.name = 'USER'", RoleEntity.class)
                .getSingleResult();
        try {
            return toDomain(users.saveAndFlush(new UserEntity(user, role)));
        } catch (DataIntegrityViolationException ex) {
            if (ex.getMostSpecificCause() instanceof java.sql.SQLException sql && "23505".equals(sql.getSQLState())) {
                throw new DuplicateAccount();
            }
            throw ex;
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        return users.findById(id).map(JpaUsers::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.findByUsername(username).map(JpaUsers::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.findByEmail(email).map(JpaUsers::toDomain);
    }

    private static User toDomain(UserEntity entity) {
        var roles = entity.roles.stream().map(role -> new Role(role.id, role.name, role.description,
                role.permissions.stream().map(p -> new Permission(p.id, p.name, p.description))
                        .collect(Collectors.toSet()))).collect(Collectors.toSet());
        return new User(entity.id, entity.username, entity.email, entity.passwordHash, entity.credentialVersion, entity.enabled,
                entity.emailVerified, entity.createdAt, entity.updatedAt, roles);
    }
}
