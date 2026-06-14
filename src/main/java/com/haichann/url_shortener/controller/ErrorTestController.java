package com.haichann.url_shortener.controller;

import com.haichann.url_shortener.exception.AppException;
import com.haichann.url_shortener.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/test/errors")
@Tag(name = "Error Testing", description = "Development endpoints for testing API error responses.")
public class ErrorTestController {

    @GetMapping
    @Operation(summary = "List error codes", description = "Returns all application error codes that can be triggered by the test endpoint.")
    public List<String> listErrorCodes() {
        return Arrays.stream(ErrorCode.values())
                .map(ErrorCode::name)
                .toList();
    }

    @GetMapping("/{code}")
    @Operation(summary = "Trigger an error code", description = "Throws the selected application error code for manual error response testing.")
    public void trigger(@PathVariable String code) {
        try {
            throw new AppException(ErrorCode.valueOf(code.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new AppException(
                    ErrorCode.INVALID_REQUEST,
                    "Unknown error code: " + code + ". Use GET /api/v1/test/errors for available values.");
        }
    }
}
