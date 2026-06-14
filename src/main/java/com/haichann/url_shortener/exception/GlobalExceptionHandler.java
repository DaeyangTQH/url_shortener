package com.haichann.url_shortener.exception;

import com.haichann.url_shortener.base.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalExceptionHandler {

    MessageSource messageSource;

    @ExceptionHandler(AppException.class)
    public ErrorResponse handleAppException(AppException exception, HttpServletRequest request) {
        var errorCode = exception.getErrorCode();

        return ErrorResponse.builder()
                .message(resolveMessage(exception.getMessage()))
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
                .map(this::resolveValidationMessage)
                .orElse(resolveMessage("validation.failed"));

        return ErrorResponse.builder()
                .message(message)
                .error("INVALID_REQUEST")
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();
    }

    private String resolveValidationMessage(FieldError fieldError) {
        return resolveMessage(fieldError.getDefaultMessage(), fieldError.getArguments());
    }

    private String resolveMessage(String messageKey, Object... args) {
        try {
            return messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException exception) {
            return messageKey;
        }
    }

}
