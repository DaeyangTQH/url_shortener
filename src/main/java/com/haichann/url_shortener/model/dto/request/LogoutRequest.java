package com.haichann.url_shortener.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogoutRequest {
    @NotBlank(message = "{validation.refresh_token.required}")
    String refreshToken;
}
