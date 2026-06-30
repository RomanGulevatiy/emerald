package me.romangulevatiy.emerald.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {

    String accessToken;
    String refreshToken;

    @Builder.Default
    String tokenType = "Bearer";

    String username;
}
