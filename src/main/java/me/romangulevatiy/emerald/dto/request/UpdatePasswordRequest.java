package me.romangulevatiy.emerald.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePasswordRequest {

    @NotBlank
    String currentPassword;

    @Size(min = 6, max = 64, message = "Password must be between {min} and {max} characters")
    @NotBlank
    String newPassword;
}