package me.romangulevatiy.emerald.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import me.romangulevatiy.emerald.entity.enums.UserRole;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    String username;
    UserRole role;
    Instant createdAt;
    Instant updatedAt;
}
