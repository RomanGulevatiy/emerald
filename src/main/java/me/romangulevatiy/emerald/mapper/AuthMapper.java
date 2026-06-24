package me.romangulevatiy.emerald.mapper;

import me.romangulevatiy.emerald.dto.AuthRequest;
import me.romangulevatiy.emerald.dto.AuthResponse;
import me.romangulevatiy.emerald.entity.UserEntity;
import me.romangulevatiy.emerald.entity.enums.UserRole;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = UserRole.class)
public interface AuthMapper {

    AuthResponse toAuthResponse(String accessToken, String refreshToken, String username);

    @Mapping(target = "username", source = "authRequest.username")
    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "role", expression = "java(UserRole.USER)")
    UserEntity toUserEntity(AuthRequest authRequest, String encodedPassword);
}
