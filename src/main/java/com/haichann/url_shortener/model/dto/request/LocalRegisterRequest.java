package com.haichann.url_shortener.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocalRegisterRequest {

    @NotBlank @Size(min = 3, max = 50)
    String username;

    @NotBlank @Email @Size(max = 254)
    String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
            message = "validation.password.invalid")
    String password;
}
