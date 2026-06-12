package me.romangulevatiy.emerald.mapper;

import me.romangulevatiy.emerald.dto.AuthRequest;
import me.romangulevatiy.emerald.dto.AuthResponse;
import me.romangulevatiy.emerald.entity.UserEntity;
import me.romangulevatiy.emerald.entity.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponse(String accessToken, String refreshToken, String username) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(username)
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
