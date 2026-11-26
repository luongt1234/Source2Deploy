package com.source2deploy.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import com.source2deploy.auth.application.port.PasswordHasher;
import com.source2deploy.auth.application.port.UserRepository;
import com.source2deploy.auth.application.service.Accounts;
import com.source2deploy.auth.domain.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountsTest {
    private final UserRepository users = mock(UserRepository.class);
    private final PasswordHasher passwords = mock(PasswordHasher.class);
    private final Instant now = Instant.parse("2026-01-01T00:00:00Z");
    private final Accounts accounts = new Accounts(users, passwords, Clock.fixed(now, ZoneOffset.UTC));

    @Test
    void normalizesIdentityAndPersistsOnlyTheHash() {
        when(passwords.hash("long test password")).thenReturn("stored-hash");
        when(users.insert(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var profile = accounts.register("DEVELOPER", "Dev@example.test", "long test password");
        var captured = ArgumentCaptor.forClass(User.class);
        verify(users).insert(captured.capture());
        assertThat(profile.username()).isEqualTo("developer");
        assertThat(profile.email()).isEqualTo("dev@example.test");
        assertThat(profile.createdAt()).isEqualTo(now);
        assertThat(captured.getValue().passwordHash()).isEqualTo("stored-hash");
        assertThat(captured.getValue().credentialVersion()).isNotNull();
    }

    @Test
    void rejectsWeakInputsBeforeHashingOrWriting() {
        assertThatThrownBy(() -> accounts.register("user", "user@example.test", "weak"))
                .isInstanceOf(Accounts.InvalidPassword.class);
        verifyNoInteractions(users, passwords);
    }
}
