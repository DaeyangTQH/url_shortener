package com.haichann.url_shortener.exception;

import com.haichann.url_shortener.base.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ErrorResponse handleAppException(AppException exception, HttpServletRequest request) {
        var errorCode = exception.getErrorCode();

        return ErrorResponse.builder()
                .message(exception.getMessage())
                .error(errorCode.name())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("Validation failed");

        return ErrorResponse.builder()
                .message(message)
                .error("INVALID_REQUEST")
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();
    }

}
