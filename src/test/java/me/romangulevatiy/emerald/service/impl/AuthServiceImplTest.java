package me.romangulevatiy.emerald.service.impl;

import me.romangulevatiy.emerald.dto.request.AuthRequest;
import me.romangulevatiy.emerald.dto.response.AuthResponse;
import me.romangulevatiy.emerald.dto.request.RefreshTokenRequest;
import me.romangulevatiy.emerald.entity.UserEntity;
import me.romangulevatiy.emerald.entity.enums.UserRole;
import me.romangulevatiy.emerald.exception.InvalidCredentialsException;
import me.romangulevatiy.emerald.exception.InvalidRefreshTokenException;
import me.romangulevatiy.emerald.exception.UsernameAlreadyExistsException;
import me.romangulevatiy.emerald.dto.mapper.AuthMapper;
import me.romangulevatiy.emerald.repository.UserRepository;
import me.romangulevatiy.emerald.security.JwtService;
import me.romangulevatiy.emerald.security.UserPrincipal;
import me.romangulevatiy.emerald.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit tests for Authentication Service implementation")
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthMapper authMapper;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @InjectMocks private AuthServiceImpl authService;

    private AuthRequest authRequest;
    private String encodedPassword;
    private UserEntity userEntity;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest("SuperUser", "Pwd123");
        encodedPassword = "encoded_Pwd123";

        userEntity = UserEntity.builder()
                .username(authRequest.getUsername())
                .password(encodedPassword)
                .role(UserRole.USER)
                .build();

        authResponse = AuthResponse.builder()
                .accessToken("eyJhP9njU.eyA1kw2wl.3_K10n")
                .refreshToken("eyJhP9njU.eyA5w2wl.9m_dQ-a")
                .tokenType("Bearer")
                .username(userEntity.getUsername())
                .build();
    }

    @DisplayName("register should encode password, save user and return AuthResponse with tokens and username")
    @Test
    void register_ShouldSaveUser_WhenRequestIsValid() {
        Long userId = 1L;

        UserEntity savedUser = UserEntity.builder()
                .id(userId)
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .role(userEntity.getRole())
                .build();
        authResponse.setUsername(savedUser.getUsername());

        when(userRepository.existsByUsername(authRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(authRequest.getPassword())).thenReturn(encodedPassword);
        when(authMapper.toUserEntity(authRequest, encodedPassword)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(savedUser);

        when(jwtService.generateAccessToken(anyMap(), any(UserPrincipal.class))).thenReturn(authResponse.getAccessToken());
        when(refreshTokenService.create(anyString())).thenReturn(authResponse.getRefreshToken());
        when(authMapper.toAuthResponse(authResponse.getAccessToken(), authResponse.getRefreshToken(), savedUser.getUsername()))
                .thenReturn(authResponse);

        AuthResponse result = authService.register(authRequest);

        assertNotNull(result);
        assertEquals(authResponse, result);
        verify(userRepository).existsByUsername(authRequest.getUsername());
        verify(passwordEncoder).encode(authRequest.getPassword());
        verify(authMapper).toUserEntity(authRequest, encodedPassword);
        verify(userRepository).save(userEntity);
        verify(authMapper).toAuthResponse(authResponse.getAccessToken(), authResponse.getRefreshToken(), savedUser.getUsername());
    }

    @DisplayName("register should throw UsernameAlreadyExistsException when username already exists")
    @Test
    void register_ShouldThrowException_WhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername(authRequest.getUsername())).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> authService.register(authRequest));
        verify(userRepository).existsByUsername(authRequest.getUsername());
    }

    @DisplayName("login should return AuthResponse with tokens and username when credentials are valid")
    @Test
    void login_ShouldReturnAuthResponse_WhenRequestIsValid() {
        when(userRepository.findByUsername(authRequest.getUsername())).thenReturn((Optional.of(userEntity)));
        when(passwordEncoder.matches(authRequest.getPassword(), userEntity.getPassword())).thenReturn(true);

        when(jwtService.generateAccessToken(anyMap(), any(UserPrincipal.class))).thenReturn(authResponse.getAccessToken());
        when(refreshTokenService.create(anyString())).thenReturn(authResponse.getRefreshToken());
        when(authMapper.toAuthResponse(authResponse.getAccessToken(), authResponse.getRefreshToken(), userEntity.getUsername()))
                .thenReturn(authResponse);

        AuthResponse result = authService.login(authRequest);

        assertNotNull(result);
        assertEquals(authResponse, result);
        verify(userRepository).findByUsername(authRequest.getUsername());
        verify(passwordEncoder).matches(authRequest.getPassword(), userEntity.getPassword());
        verify(authMapper).toAuthResponse(authResponse.getAccessToken(), authResponse.getRefreshToken(), userEntity.getUsername());
    }

    @DisplayName("login should throw InvalidCredentialsException when username is not found")
    @Test
    void login_ShouldThrowException_WhenUsernameNotFound() {
        when(userRepository.findByUsername(authRequest.getUsername())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(authRequest));
        verify(userRepository).findByUsername(authRequest.getUsername());
    }

    @DisplayName("login should throw InvalidCredentialsException when password is invalid")
    @Test
    void login_ShouldThrowException_WhenPasswordIsInvalid() {
        when(userRepository.findByUsername(authRequest.getUsername())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(authRequest.getPassword(), userEntity.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(authRequest));
        verify(userRepository).findByUsername(authRequest.getUsername());
    }

    @DisplayName("logout should delete refresh token when request is valid")
    @Test
    void logout_ShouldDeleteRefreshToken_WhenRequestIsValid() {
        String refreshToken = "RefreshToken";
        String username = "SuperUser";
        String authHeader = "Bearer AccessToken";
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshToken);

        when(refreshTokenService.extractUsername(refreshToken)).thenReturn(username);

        authService.logout(authHeader, refreshTokenRequest);

        verify(refreshTokenService).extractUsername(refreshToken);
        verify(refreshTokenService).delete(refreshToken);
    }

    @DisplayName("logout should throw InvalidRefreshTokenException when request is invalid")
    @Test
    void logout_ShouldThrowInvalidRefreshTokenException_WhenRequestIsInvalid() {
        String invalidRefreshToken = "InvalidRefreshToken";
        String authHeader = "Bearer AccessToken";
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(invalidRefreshToken);

        when(refreshTokenService.extractUsername(invalidRefreshToken))
                .thenThrow(new InvalidRefreshTokenException("Refresh token is invalid or expired"));

        assertThrows(InvalidRefreshTokenException.class, () -> authService.logout(authHeader, refreshTokenRequest));
        verify(refreshTokenService).extractUsername(invalidRefreshToken);
        verify(refreshTokenService, never()).delete(anyString());
    }

    @DisplayName("refresh should regenerate and return new tokens when refresh token is valid")
    @Test
    void refresh_ShouldRegenerateTokens_WhenRefreshTokenIsValid() {
        String oldRefreshToken = authResponse.getRefreshToken();
        String username = userEntity.getUsername();

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(oldRefreshToken);

        String newAccessToken = "new.Access.Token";
        String newRefreshToken = "new.Refresh.Token";

        AuthResponse expectedAuthResponse = AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .username(username)
                .build();

        when(refreshTokenService.extractUsername(oldRefreshToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        when(jwtService.generateAccessToken(anyMap(), any(UserPrincipal.class))).thenReturn(newAccessToken);
        when(refreshTokenService.create(anyString())).thenReturn(newRefreshToken);
        when(authMapper.toAuthResponse(newAccessToken, newRefreshToken, username)).thenReturn(expectedAuthResponse);

        AuthResponse result = authService.refresh(refreshTokenRequest);

        assertNotNull(result);
        assertEquals(expectedAuthResponse, result);
        verify(refreshTokenService).extractUsername(oldRefreshToken);
        verify(userRepository).findByUsername(username);
        verify(jwtService).generateAccessToken(anyMap(), any(UserPrincipal.class));
        verify(refreshTokenService).delete(oldRefreshToken);
        verify(refreshTokenService).create(username);
        verify(authMapper).toAuthResponse(newAccessToken, newRefreshToken, username);
    }

    @DisplayName("refresh should throw InvalidRefreshTokenException when username cannot be extracted from refresh token")
    @Test
    void refresh_ShouldThrowException_WhenUsernameCannotBeExtractedFromRefreshToken() {
        String oldRefreshToken = "invalid.Refresh.Token";
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(oldRefreshToken);

        when(refreshTokenService.extractUsername(oldRefreshToken))
                .thenThrow(new InvalidRefreshTokenException("Refresh token is invalid or expired"));

        assertThrows(InvalidRefreshTokenException.class, () -> authService.refresh(refreshTokenRequest));
        verify(refreshTokenService).extractUsername(oldRefreshToken);
        verifyNoInteractions(userRepository);
    }

    @DisplayName("refresh should throw InvalidRefreshTokenException when user is not found for extracted username")
    @Test
    void refresh_ShouldThrowException_WhenUserNotFoundForRefreshToken() {
        String oldRefreshToken = "invalid.Refresh.Token";
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(oldRefreshToken);
        String username = "NonExistentUser";

        when(refreshTokenService.extractUsername(oldRefreshToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(InvalidRefreshTokenException.class, () -> authService.refresh(refreshTokenRequest));
        verify(refreshTokenService).extractUsername(oldRefreshToken);
        verify(userRepository).findByUsername(username);
        verify(jwtService, never()).generateAccessToken(anyMap(), any(UserPrincipal.class));
        verify(refreshTokenService, never()).create(anyString());
    }
}
