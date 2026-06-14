package com.haichann.url_shortener.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper.")
public class ApiResponse<T> {

    @Schema(description = "Human-readable response message.", example = "Password changed successfully")
    String message;

    @Schema(description = "Business response payload.")
    T responseData;
}
