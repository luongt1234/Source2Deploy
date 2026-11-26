package com.source2deploy.auth.infrastructure.email;

import java.time.Clock;
import java.util.concurrent.ThreadPoolExecutor;

import com.source2deploy.auth.application.port.AccountTokens;
import com.source2deploy.auth.application.port.AccountTokens.Purpose;
import com.source2deploy.auth.application.port.UserRepository;
import com.source2deploy.auth.application.port.RecoveryRequests;
import com.source2deploy.auth.application.service.AccountRecovery;
import com.source2deploy.auth.infrastructure.security.TokenHashes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;

class RecoveryDelivery implements RecoveryRequests {
    private static final Logger LOG = LoggerFactory.getLogger(RecoveryDelivery.class);

    private final AccountRecovery recovery;
    private final JavaMailSender mail;
    private final String from;
    private final ThreadPoolTaskExecutor executor;

    RecoveryDelivery(AccountRecovery recovery, JavaMailSender mail, String from, ThreadPoolTaskExecutor executor) {
        if (from.isBlank()) {
            throw new IllegalArgumentException("Configure AUTH_MAIL_FROM when enabling account recovery");
        }
        this.recovery = recovery;
        this.mail = mail;
        this.from = from;
        this.executor = executor;
    }

    @Override
    public void request(String email, Purpose purpose) {
        try {
            executor.execute(() -> {
                try {
                    recovery.issue(email, purpose, (recipient, token) -> send(recipient, purpose, token));
                } catch (RuntimeException ex) {
                    LOG.warn("Account email could not be processed");
                }
            });
        } catch (TaskRejectedException ex) {
            LOG.warn("Account email queue is full");
        }
    }

    private void send(String recipient, Purpose purpose, String token) {
        var message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject(purpose == Purpose.VERIFY_EMAIL ? "Verify your Source2Deploy email" : "Reset your Source2Deploy password");
        message.setText("Enter this one-time code in the Source2Deploy client:\n\n" + token);
        mail.send(message);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "auth.recovery.enabled", havingValue = "true")
    static class ConfigurationBeans {
        @Bean
        AccountRecovery accountRecovery(UserRepository users, AccountTokens tokens, PasswordEncoder passwords, Clock clock) {
            return new AccountRecovery(users, tokens, passwords::encode, TokenHashes::sha256, clock);
        }

        @Bean
        ThreadPoolTaskExecutor recoveryExecutor() {
            var executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(1);
            executor.setMaxPoolSize(2);
            executor.setQueueCapacity(100);
            executor.setThreadNamePrefix("account-email-");
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
            return executor;
        }

        @Bean
        RecoveryRequests recoveryRequests(AccountRecovery recovery, JavaMailSender mail, ThreadPoolTaskExecutor executor,
                                          @Value("${auth.recovery.from}") String from,
                                          @Value("${spring.mail.host:}") String host) {
            if (host.isBlank()) {
                throw new IllegalArgumentException("Configure SPRING_MAIL_HOST when enabling account recovery");
            }
            return new RecoveryDelivery(recovery, mail, from, executor);
        }
    }
}
