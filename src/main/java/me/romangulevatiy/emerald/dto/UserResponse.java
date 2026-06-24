package me.romangulevatiy.emerald.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.romangulevatiy.emerald.entity.enums.UserRole;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String username;
    private UserRole role;
    private Instant createdAt;
    private Instant updatedAt;
}
