package com.source2deploy.auth.infrastructure.config;

import java.time.Clock;

import com.source2deploy.auth.application.port.UserRepository;
import com.source2deploy.auth.application.service.Accounts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration(proxyBeanMethods = false)
class IdentityConfig {
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    Accounts accounts(UserRepository users, PasswordEncoder passwords, Clock clock) {
        return new Accounts(users, passwords::encode, clock);
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository users) {
        return username -> {
            var account = users.findByUsername(Accounts.normalize(username))
                    .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
            return User.withUsername(account.id().toString())
                    .password(account.passwordHash())
                    .disabled(!account.enabled())
                    .authorities("SESSION_" + account.credentialVersion())
                    .build();
        };
    }
}
