package me.romangulevatiy.emerald.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.romangulevatiy.emerald.dto.AuthRequest;
import me.romangulevatiy.emerald.dto.AuthResponse;
import me.romangulevatiy.emerald.dto.RefreshTokenRequest;
import me.romangulevatiy.emerald.exception.InvalidCredentialsException;
import me.romangulevatiy.emerald.exception.InvalidRefreshTokenException;
import me.romangulevatiy.emerald.security.*;
import me.romangulevatiy.emerald.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for Authentication API endpoints")
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class,
        UserPrincipalService.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class})
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AuthService authService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserPrincipalService userPrincipalService;

    @DisplayName("POST /api/auth/register should return 201 Created with tokens and username")
    @Test
    void register_ShouldReturn201_WhenRequestIsValid() throws Exception {
        AuthRequest authRequest = new AuthRequest("SuperUser", "Pwd123");
        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("AccessToken")
                .refreshToken("RefreshToken")
                .tokenType("Bearer")
                .username(authRequest.getUsername())
                .build();

        String jsonRequest = objectMapper.writeValueAsString(authRequest);

        when(authService.register(any(AuthRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("AccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("RefreshToken"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("SuperUser"));
    }

    @DisplayName("POST /api/auth/register should return 400 Bad Request when input is invalid")
    @Test
    void register_ShouldReturn400_WhenRequestIsInvalid() throws Exception {
        AuthRequest authRequest = new AuthRequest("R_", "123");
        String jsonRequest = objectMapper.writeValueAsString(authRequest);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.path").value("/auth/register"))

                .andExpect(jsonPath("$.message", containsString("username: Username must contain only letters and numbers")))
                .andExpect(jsonPath("$.message", containsString("password: Password must be between 6 and 64 characters")))
                .andExpect(jsonPath("$.message", containsString("username: Username must be between 3 and 16 characters")));
    }

    @DisplayName("POST /api/auth/login should return 200 OK with tokens and username")
    @Test
    void login_ShouldReturn200_WhenRequestIsValid() throws Exception {
        AuthRequest authRequest = new AuthRequest("SuperUser", "Pwd123");
        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("AccessToken")
                .refreshToken("RefreshToken")
                .tokenType("Bearer")
                .username(authRequest.getUsername())
                .build();

        String jsonRequest = objectMapper.writeValueAsString(authRequest);

        when(authService.login(any(AuthRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("AccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("RefreshToken"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("SuperUser"));
    }

    @DisplayName("POST /api/auth/login should return 401 Unauthorized when username or password is invalid")
    @Test
    void login_ShouldReturn401_WhenUsernameOrPasswordIsInvalid() throws Exception {
        AuthRequest authRequest = new AuthRequest("InvalidUser", "WrongPassword");
        String jsonRequest = objectMapper.writeValueAsString(authRequest);

        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.error").value("Invalid username or password"))
                .andExpect(jsonPath("$.message").value("The provided credentials are incorrect"))
                .andExpect(jsonPath("$.path").value("/auth/login"));
    }

    @DisplayName("POST /api/auth/logout should return 204 No Content when request is valid")
    @Test
    void logout_ShouldReturn204_WhenRequestIsValid() throws Exception {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("RefreshToken");
        String jsonRequest = objectMapper.writeValueAsString(refreshTokenRequest);

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNoContent());

        verify(authService).logout(refreshTokenRequest);
    }

    @DisplayName("POST /api/auth/logout should return 401 Unauthorized when request is invalid")
    @Test
    void logout_ShouldReturn401_WhenRequestIsInvalid() throws Exception {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("InvalidRefreshToken");
        String jsonRequest = objectMapper.writeValueAsString(refreshTokenRequest);

        doThrow(new InvalidRefreshTokenException("Refresh token is invalid or expired"))
                .when(authService).logout(any(RefreshTokenRequest.class));

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.error").value("Invalid or expired token"))
                .andExpect(jsonPath("$.message").value("Refresh token is invalid or expired"))
                .andExpect(jsonPath("$.path").value("/auth/logout"));
    }

    @DisplayName("POST /api/auth/refresh should return 200 OK with new tokens and username")
    @Test
    void refresh_ShouldReturn200_WhenRefreshTokenIsValid() throws Exception {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("OldRefreshToken");
        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("NewAccessToken")
                .refreshToken("NewRefreshToken")
                .tokenType("Bearer")
                .username("SuperUser")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(refreshTokenRequest);

        when(authService.refresh(any(RefreshTokenRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("NewAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("NewRefreshToken"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("SuperUser"));
    }

    @DisplayName("POST /api/auth/refresh should return 401 Unauthorized when refresh token is invalid or expired")
    @Test
    void refresh_ShouldReturn401_WhenRefreshTokenIsInvalid() throws Exception {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("InvalidRefreshToken");
        String jsonRequest = objectMapper.writeValueAsString(refreshTokenRequest);

        when(authService.refresh(any(RefreshTokenRequest.class)))
                .thenThrow(new InvalidRefreshTokenException("Refresh token is invalid or expired"));

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.error").value("Invalid or expired token"))
                .andExpect(jsonPath("$.message").value("Refresh token is invalid or expired"))
                .andExpect(jsonPath("$.path").value("/auth/refresh"));
    }
}