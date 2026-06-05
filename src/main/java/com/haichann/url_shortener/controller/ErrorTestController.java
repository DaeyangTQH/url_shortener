package com.haichann.url_shortener.controller;

import com.haichann.url_shortener.exception.AppException;
import com.haichann.url_shortener.exception.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/test/errors")
public class ErrorTestController {

    @GetMapping
    public List<String> listErrorCodes() {
        return Arrays.stream(ErrorCode.values())
                .map(ErrorCode::name)
                .toList();
    }

    @GetMapping("/{code}")
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
