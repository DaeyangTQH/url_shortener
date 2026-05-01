package com.haichann.url_shortener.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_REQUEST(1000, "Invalid request", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1001, "Authentication is required", HttpStatus.UNAUTHORIZED),
    PERMISSION_DENIED(1002, "Permission denied", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND(1003, "Resource not found", HttpStatus.NOT_FOUND),
    CONFLICT(1004, "Resource conflict", HttpStatus.CONFLICT),
    VALIDATION_ERROR(1005, "Validation failed", HttpStatus.UNPROCESSABLE_ENTITY),
    RATE_LIMITED(1006, "Too many requests", HttpStatus.TOO_MANY_REQUESTS),
    SERVICE_UNAVAILABLE(1007, "Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    INTERNAL_ERROR(1999, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized request", HttpStatus.UNAUTHORIZED);

    private final int code;
    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}
