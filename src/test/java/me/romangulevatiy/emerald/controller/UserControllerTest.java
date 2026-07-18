package me.romangulevatiy.emerald.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.romangulevatiy.emerald.dto.request.UpdatePasswordRequest;
import me.romangulevatiy.emerald.entity.UserEntity;
import me.romangulevatiy.emerald.entity.enums.UserRole;
import me.romangulevatiy.emerald.exception.EntityNotFoundException;
import me.romangulevatiy.emerald.exception.InvalidCredentialsException;
import me.romangulevatiy.emerald.security.JwtAccessDeniedHandler;
import me.romangulevatiy.emerald.security.JwtAuthenticationEntryPoint;
import me.romangulevatiy.emerald.security.JwtAuthenticationFilter;
import me.romangulevatiy.emerald.security.JwtService;
import me.romangulevatiy.emerald.security.SecurityConfig;
import me.romangulevatiy.emerald.security.UserPrincipal;
import me.romangulevatiy.emerald.security.UserPrincipalService;
import me.romangulevatiy.emerald.service.TokenBlacklistService;
import me.romangulevatiy.emerald.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for User API endpoints")
@WebMvcTest(UserController.class)
@Import({SecurityConfig.class,
        UserPrincipalService.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class})
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserService userService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private TokenBlacklistService tokenBlacklistService;
    @MockitoBean private UserPrincipalService userPrincipalService;

    @DisplayName("PATCH /api/me/password should return 204 No Content when request is valid")
    @Test
    void updatePassword_ShouldReturn204_WhenRequestIsValid() throws Exception {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("CurrentPassword")
                .newPassword("NewPassword")
                .build();

        mockMvc.perform(patch("/me/password")
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(userService).updatePassword("SuperUser", request);
    }

    @DisplayName("PATCH /api/me/password should return 400 Bad Request when request body is invalid")
    @Test
    void updatePassword_ShouldReturn400_WhenRequestBodyIsInvalid() throws Exception {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("")
                .newPassword("123")
                .build();

        mockMvc.perform(patch("/me/password")
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.path").value("/me/password"))
                .andExpect(jsonPath("$.message", containsString("currentPassword: must not be blank")))
                .andExpect(jsonPath("$.message", containsString("newPassword: Password must be between 6 and 64 characters")));

        verifyNoInteractions(userService);
    }

    @DisplayName("PATCH /api/me/password should return 401 Unauthorized when request is unauthenticated")
    @Test
    void updatePassword_ShouldReturn401_WhenRequestIsUnauthenticated() throws Exception {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("CurrentPassword")
                .newPassword("NewPassword")
                .build();

        mockMvc.perform(patch("/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

    @DisplayName("PATCH /api/me/password should return 404 Not Found when user does not exist")
    @Test
    void updatePassword_ShouldReturn404_WhenUserDoesNotExist() throws Exception {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("CurrentPassword")
                .newPassword("NewPassword")
                .build();

        doThrow(new EntityNotFoundException("User not found"))
                .when(userService).updatePassword("SuperUser", request);

        mockMvc.perform(patch("/me/password")
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("Entity not found"))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.path").value("/me/password"));

        verify(userService).updatePassword("SuperUser", request);
    }

    @DisplayName("PATCH /api/me/password should return 401 Unauthorized when current password is invalid")
    @Test
    void updatePassword_ShouldReturn401_WhenCurrentPasswordIsInvalid() throws Exception {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("WrongPassword")
                .newPassword("NewPassword")
                .build();

        doThrow(new InvalidCredentialsException("Current password is incorrect"))
                .when(userService).updatePassword("SuperUser", request);

        mockMvc.perform(patch("/me/password")
                        .with(authenticatedUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.error").value("Invalid username or password"))
                .andExpect(jsonPath("$.message").value("The provided credentials are incorrect"))
                .andExpect(jsonPath("$.path").value("/me/password"));

        verify(userService).updatePassword("SuperUser", request);
    }

    private RequestPostProcessor authenticatedUser() {
        UserPrincipal principal = new UserPrincipal(UserEntity.builder()
                .id(1L)
                .username("SuperUser")
                .password("EncodedPassword")
                .role(UserRole.USER)
                .build());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        return authentication(auth);
    }
}
