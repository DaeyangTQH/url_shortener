package com.haichann.url_shortener.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_REQUEST("error.invalid_request", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED("error.unauthenticated", HttpStatus.UNAUTHORIZED),
    PERMISSION_DENIED("error.permission_denied", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND("error.resource_not_found", HttpStatus.NOT_FOUND),
    CONFLICT("error.conflict", HttpStatus.CONFLICT),
    RATE_LIMITED("error.rate_limited", HttpStatus.TOO_MANY_REQUESTS),
    SERVICE_UNAVAILABLE("error.service_unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    INTERNAL_ERROR("error.internal_error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNCATEGORIZED_EXCEPTION("error.uncategorized_exception", HttpStatus.INTERNAL_SERVER_ERROR),

    INVALID_CREDENTIALS("error.invalid_credentials", HttpStatus.UNAUTHORIZED),

    TOKEN_EXPIRED("error.token_expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("error.invalid_token", HttpStatus.UNAUTHORIZED);

    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(String message, HttpStatus statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

}
