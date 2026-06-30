package me.romangulevatiy.emerald.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    String refreshToken;
}
