package com.haichann.url_shortener.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_REQUEST("Invalid request", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED("Authentication is required", HttpStatus.UNAUTHORIZED),
    PERMISSION_DENIED("Permission denied", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND("Resource not found", HttpStatus.NOT_FOUND),
    CONFLICT("Resource conflict", HttpStatus.CONFLICT),
    RATE_LIMITED("Too many requests", HttpStatus.TOO_MANY_REQUESTS),
    SERVICE_UNAVAILABLE("Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    INTERNAL_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNCATEGORIZED_EXCEPTION("Uncategorized request", HttpStatus.INTERNAL_SERVER_ERROR),

    INVALID_CREDENTIALS("Invalid email or password", HttpStatus.UNAUTHORIZED),

    TOKEN_EXPIRED("Token expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("Invalid token", HttpStatus.UNAUTHORIZED);

    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(String message, HttpStatus statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

}
