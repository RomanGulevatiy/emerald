package me.romangulevatiy.emerald.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRequest {

    @Size(min = 3, max = 16, message = "Username must be between {min} and {max} characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must contain only letters and numbers")
    @NotBlank
    String username;

    @Size(min = 6, max = 64, message = "Password must be between {min} and {max} characters")
    @NotBlank
    String password;
}