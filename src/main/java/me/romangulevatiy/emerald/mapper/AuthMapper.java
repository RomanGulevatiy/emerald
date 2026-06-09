package me.romangulevatiy.emerald.mapper;

import me.romangulevatiy.emerald.dto.AuthRequest;
import me.romangulevatiy.emerald.dto.AuthResponse;
import me.romangulevatiy.emerald.entity.UserEntity;
import me.romangulevatiy.emerald.entity.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponse(UserEntity user) {

        // TODO: Implement the logic to generate access and refresh tokens
        return AuthResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .tokenType(null)
                .build();
    }

    public UserEntity toUserEntity(AuthRequest authRequest, String encodedPassword) {
        return UserEntity.builder()
                .username(authRequest.getUsername())
                .password(encodedPassword)
                .role(UserRole.USER)
                .build();
    }
}
