package com.source2deploy.auth.infrastructure.persistence;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import com.source2deploy.auth.application.port.AccountTokens;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
class JdbcAccountTokens implements AccountTokens {
    private final JdbcTemplate jdbc;

    JdbcAccountTokens(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean replace(String hash, UUID user, Purpose purpose, Instant expiresAt) {
        return jdbc.update("""
                INSERT INTO auth_account_token(token_hash, user_id, purpose, expires_at) VALUES (?, ?, ?, ?)
                ON CONFLICT (user_id, purpose)
                DO UPDATE SET token_hash = excluded.token_hash, expires_at = excluded.expires_at, issued_at = now()
                WHERE auth_account_token.issued_at <= now() - INTERVAL '1 minute'
                """, hash, user, purpose.name(), Timestamp.from(expiresAt)) == 1;
    }

    @Override
    @Transactional
    public boolean verify(String hash) {
        UUID user = consume(hash, Purpose.VERIFY_EMAIL);
        return user != null && jdbc.update("""
                UPDATE auth_user SET email_verified = true, updated_at = now() WHERE id = ? AND enabled
                """, user) == 1;
    }

    @Override
    @Transactional
    public boolean reset(String hash, String passwordHash) {
        UUID user = consume(hash, Purpose.RESET_PASSWORD);
        if (user == null || jdbc.update("""
                UPDATE auth_user SET password_hash = ?, credential_version = ?, updated_at = now()
                WHERE id = ? AND enabled
                """, passwordHash, UUID.randomUUID(), user) != 1) {
            return false;
        }
        // Existing access JWTs age out independently at resource servers.
        jdbc.update("DELETE FROM oauth2_authorization WHERE principal_name = ?", user.toString());
        return true;
    }

    private UUID consume(String hash, Purpose purpose) {
        return jdbc.query("""
                DELETE FROM auth_account_token WHERE token_hash = ? AND purpose = ? AND expires_at > now()
                RETURNING user_id
                """, rs -> rs.next() ? rs.getObject("user_id", UUID.class) : null, hash, purpose.name());
    }
}
