package com.source2deploy.auth.application.service;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import com.source2deploy.auth.application.port.PasswordHasher;
import com.source2deploy.auth.application.port.UserRepository;
import com.source2deploy.auth.domain.model.Permission;
import com.source2deploy.auth.domain.model.Role;
import com.source2deploy.auth.domain.model.User;

public class Accounts {
    private final UserRepository users;
    private final PasswordHasher passwords;
    private final Clock clock;

    public Accounts(UserRepository users, PasswordHasher passwords, Clock clock) {
        this.users = users;
        this.passwords = passwords;
        this.clock = clock;
    }

    public Profile register(String username, String email, String password) {
        validatePassword(password);
        var now = clock.instant();
        return profile(users.insert(new User(UUID.randomUUID(), normalize(username), normalize(email),
                passwords.hash(password), UUID.randomUUID(), true, false, now, now, Set.of())));
    }

    public Profile current(String subject) {
        return profile(activeUser(subject));
    }

    public Authorities authorities(String subject) {
        return authorities(activeUser(subject));
    }

    public User forSession(String subject, Set<String> sessionAuthorities) {
        var user = activeUser(subject);
        if (!sessionAuthorities.contains("SESSION_" + user.credentialVersion())) {
            throw new InactiveAccount();
        }
        return user;
    }

    private User activeUser(String subject) {
        try {
            return users.findById(UUID.fromString(subject)).filter(User::enabled)
                    .orElseThrow(InactiveAccount::new);
        } catch (IllegalArgumentException ex) {
            throw new InactiveAccount();
        }
    }

    private Profile profile(User user) {
        return new Profile(user.id(), user.username(), user.email(), user.emailVerified(), user.createdAt());
    }

    public static Authorities authorities(User user) {
        var roles = user.roles().stream().map(Role::name).collect(Collectors.toCollection(TreeSet::new));
        var permissions = user.roles().stream().flatMap(role -> role.permissions().stream())
                .map(Permission::name).collect(Collectors.toCollection(TreeSet::new));
        return new Authorities(roles, permissions);
    }

    public static String normalize(String value) {
        return value.strip().toLowerCase(Locale.ROOT);
    }

    public static void validatePassword(String password) {
        if (password == null || password.codePointCount(0, password.length()) < 12
                || password.getBytes(StandardCharsets.UTF_8).length > 72 || password.isBlank()) {
            throw new InvalidPassword();
        }
    }

    public record Profile(UUID id, String username, String email, boolean emailVerified, java.time.Instant createdAt) {}
    public record Authorities(Set<String> roles, Set<String> permissions) {}

    public static class DuplicateAccount extends RuntimeException {}
    public static class InactiveAccount extends RuntimeException {}
    public static class InvalidPassword extends RuntimeException {}
}
