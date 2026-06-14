package com.haichann.url_shortener.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Local account registration request.")
public class LocalRegisterRequest {

    @Schema(description = "Unique username.", example = "haichan", minLength = 3, maxLength = 50)
    @NotBlank(message = "validation.not_blank")
    @Size(min = 3, max = 50, message = "validation.username.size")
    String username;

    @Schema(description = "Unique email address.", example = "haichan@example.com", maxLength = 254)
    @NotBlank(message = "validation.not_blank")
    @Email(message = "validation.email.invalid")
    @Size(max = 254, message = "validation.max_length")
    String email;

    @Schema(description = "Password with uppercase, lowercase, number, and special character.", example = "Password@123", minLength = 8)
    @NotBlank(message = "validation.not_blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
            message = "validation.password.invalid")
    String password;
}
