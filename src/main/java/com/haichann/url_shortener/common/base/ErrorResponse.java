package com.haichann.url_shortener.common.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API error response.")
public class ErrorResponse {

    @Schema(description = "Human-readable error message.", example = "Invalid email or password")
    String message;

    @Schema(description = "Application error code.", example = "INVALID_CREDENTIALS")
    String error;

    @Schema(description = "Error timestamp in UTC.", example = "2026-06-11T08:30:00Z")
    Instant timestamp;

    @Schema(description = "Request path that produced the error.", example = "/api/v1/auth/login")
    String path;
}
