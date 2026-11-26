package com.source2deploy.auth.application.port;

import java.util.Optional;
import java.util.UUID;

import com.source2deploy.auth.domain.model.User;

public interface UserRepository {
    User insert(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
