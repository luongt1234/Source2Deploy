package com.source2deploy.auth.presentation.controller;

import com.source2deploy.auth.application.service.Accounts;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AccountController {
    private final Accounts accounts;

    AccountController(Accounts accounts) {
        this.accounts = accounts;
    }

    @PostMapping(value = "/api/v1/auth/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    Accounts.Profile register(@Valid @RequestBody Registration request) {
        return accounts.register(request.username(), request.email(), request.password());
    }

    @GetMapping("/api/v1/users/me")
    Accounts.Profile currentUser(@AuthenticationPrincipal Jwt token) {
        return accounts.current(token.getSubject());
    }

    @GetMapping("/api/v1/users/me/permissions")
    Accounts.Authorities permissions(@AuthenticationPrincipal Jwt token) {
        return accounts.authorities(token.getSubject());
    }

    record Registration(@NotBlank @Pattern(regexp = "[A-Za-z0-9_.-]{3,64}") String username,
                        @NotBlank @Email @Size(max = 254) String email,
                        @NotBlank @Size(max = 72) String password) {
        @Override
        public String toString() {
            return "Registration[redacted]";
        }
    }
}
