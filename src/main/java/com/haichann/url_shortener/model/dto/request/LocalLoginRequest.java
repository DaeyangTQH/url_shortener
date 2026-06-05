package com.haichann.url_shortener.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocalLoginRequest {

    @NotBlank(message = "validation.not_blank")
    @NotNull(message = "validation.not_null")
    @Size(max = 254, message = "validation.max_length")
    @Email(message = "validation.email.invalid")
    String email;

    @NotBlank(message = "validation.not_blank")
    @NotNull(message = "validation.not_null")
    @Size(min = 8, message = "validation.min_length")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
            message = "validation.password.invalid"
    )
    String password;
}
