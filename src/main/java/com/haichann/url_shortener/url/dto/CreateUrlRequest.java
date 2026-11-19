package com.haichann.url_shortener.url.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request to create a shortened URL.")
public class CreateUrlRequest {

    @Schema(
            description = "Original URL to shorten. Must use http or https.",
            example = "https://example.com/very/long/path",
            maxLength = 2048
    )
    @NotBlank(message = "validation.not_blank")
    @Size(max = 2048, message = "validation.max_length")
    @Pattern(
            regexp = "^(?i)https?://.+$",
            message = "validation.url.invalid"
    )
    String originalUrl;

    @Schema(
            description = "Optional expiration timestamp. Defaults to 30 days from creation when omitted.",
            example = "2026-12-31T23:59:59"
    )
    @Future(message = "validation.url.expiration_future")
    LocalDateTime expirationDate;
}
