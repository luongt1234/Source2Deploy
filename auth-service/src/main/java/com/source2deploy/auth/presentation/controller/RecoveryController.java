package com.source2deploy.auth.presentation.controller;

import com.source2deploy.auth.application.port.AccountTokens.Purpose;
import com.source2deploy.auth.application.service.AccountRecovery;
import com.source2deploy.auth.application.port.RecoveryRequests;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@ConditionalOnProperty(name = "auth.recovery.enabled", havingValue = "true")
class RecoveryController {
    private final AccountRecovery recovery;
    private final RecoveryRequests delivery;

    RecoveryController(AccountRecovery recovery, RecoveryRequests delivery) {
        this.recovery = recovery;
        this.delivery = delivery;
    }

    @PostMapping(value = "/api/v1/auth/request-verification", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    void requestVerification(@Valid @RequestBody EmailRequest request) {
        delivery.request(request.email(), Purpose.VERIFY_EMAIL);
    }

    @PostMapping(value = "/api/v1/auth/forgot-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    void forgotPassword(@Valid @RequestBody EmailRequest request) {
        delivery.request(request.email(), Purpose.RESET_PASSWORD);
    }

    @PostMapping(value = "/api/v1/auth/verify-email", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void verify(@Valid @RequestBody VerifyRequest request) {
        if (!recovery.verify(request.token())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired code");
        }
    }

    @PostMapping(value = "/api/v1/auth/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void reset(@Valid @RequestBody ResetRequest request) {
        if (!recovery.reset(request.token(), request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired code");
        }
    }

    record EmailRequest(@NotBlank @Email @Size(max = 254) String email) {}

    record VerifyRequest(@NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{43}") String token) {
        @Override public String toString() { return "VerifyRequest[redacted]"; }
    }

    record ResetRequest(@NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{43}") String token,
                        @NotBlank @Size(max = 72) String password) {
        @Override public String toString() { return "ResetRequest[redacted]"; }
    }
}
