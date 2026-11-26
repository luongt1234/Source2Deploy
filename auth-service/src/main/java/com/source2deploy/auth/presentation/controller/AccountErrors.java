package com.source2deploy.auth.presentation.controller;

import com.source2deploy.auth.application.service.Accounts;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class AccountErrors {
    @ExceptionHandler(Accounts.InvalidPassword.class)
    ProblemDetail invalidPassword() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Password must contain at least 12 characters and at most 72 UTF-8 bytes.");
    }

    @ExceptionHandler(Accounts.DuplicateAccount.class)
    ProblemDetail duplicate() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Account could not be registered.");
    }

    @ExceptionHandler(Accounts.InactiveAccount.class)
    ProblemDetail inactive() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Account is unavailable.");
    }
}
