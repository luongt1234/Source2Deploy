package com.source2deploy.auth.application.service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.function.BiConsumer;

import com.source2deploy.auth.application.port.AccountTokens;
import com.source2deploy.auth.application.port.AccountTokens.Purpose;
import com.source2deploy.auth.application.port.PasswordHasher;
import com.source2deploy.auth.application.port.UserRepository;
import com.source2deploy.auth.domain.model.User;

public class AccountRecovery {
    private final SecureRandom random = new SecureRandom();
    private final UserRepository users;
    private final AccountTokens tokens;
    private final PasswordHasher passwords;
    private final java.util.function.Function<String, String> tokenHash;
    private final Clock clock;

    public AccountRecovery(UserRepository users, AccountTokens tokens, PasswordHasher passwords,
                           java.util.function.Function<String, String> tokenHash, Clock clock) {
        this.users = users;
        this.tokens = tokens;
        this.passwords = passwords;
        this.tokenHash = tokenHash;
        this.clock = clock;
    }

    public void issue(String email, Purpose purpose, BiConsumer<String, String> deliver) {
        Optional<User> user = users.findByEmail(Accounts.normalize(email)).filter(User::enabled)
                .filter(account -> purpose != Purpose.VERIFY_EMAIL || !account.emailVerified());
        user.ifPresent(account -> {
            byte[] bytes = new byte[32];
            random.nextBytes(bytes);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            Duration lifetime = purpose == Purpose.VERIFY_EMAIL ? Duration.ofHours(24) : Duration.ofMinutes(20);
            if (tokens.replace(tokenHash.apply(token), account.id(), purpose, clock.instant().plus(lifetime))) {
                deliver.accept(account.email(), token);
            }
        });
    }

    public boolean verify(String token) {
        return tokens.verify(tokenHash.apply(token));
    }

    public boolean reset(String token, String password) {
        Accounts.validatePassword(password);
        return tokens.reset(tokenHash.apply(token), passwords.hash(password));
    }
}
