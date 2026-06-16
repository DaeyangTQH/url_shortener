package com.haichann.url_shortener.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Authentication result containing the issued JWT access token.")
public class AuthenticationResponse {

    @Schema(description = "Whether authentication succeeded.", example = "true")
    boolean authenticated;

    @Schema(description = "JWT access token used as a Bearer token for authenticated endpoints.")
    String accessToken;

    @Schema(description = "Refresh token used to obtain new access tokens without re-authenticating.")
    String refreshToken;
}
