package com.haichann.url_shortener.common.exception;

import com.haichann.url_shortener.common.base.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalExceptionHandler {

    MessageSource messageSource;

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException exception, HttpServletRequest request) {
        var errorCode = exception.getErrorCode();

        ErrorResponse body = ErrorResponse.builder()
                .message(resolveMessage(exception.getMessage()))
                .error(errorCode.name())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(this::resolveValidationMessage)
                .orElse(resolveMessage("validation.failed"));

        ErrorResponse body = ErrorResponse.builder()
                .message(message)
                .error("INVALID_REQUEST")
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private String resolveValidationMessage(FieldError fieldError) {
        return resolveMessage(fieldError.getDefaultMessage(), fieldError.getArguments());
    }

    private String resolveMessage(String messageKey, Object... args) {
        if (messageKey == null || messageKey.isBlank()) {
            return messageKey;
        }
        // Prefer explicit default message so a missing bundle never surfaces the raw key silently.
        Locale locale = LocaleContextHolder.getLocale();
        String resolved = messageSource.getMessage(messageKey, args, messageKey, locale);
        if (!messageKey.equals(resolved)) {
            return resolved;
        }
        return messageSource.getMessage(messageKey, args, messageKey, Locale.ENGLISH);
    }

}
