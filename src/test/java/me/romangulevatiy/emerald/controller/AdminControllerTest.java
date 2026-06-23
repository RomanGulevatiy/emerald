package me.romangulevatiy.emerald.controller;

import me.romangulevatiy.emerald.dto.PageResponse;
import me.romangulevatiy.emerald.dto.UserResponse;
import me.romangulevatiy.emerald.entity.enums.UserRole;
import me.romangulevatiy.emerald.exception.EntityNotFoundException;
import me.romangulevatiy.emerald.security.*;
import me.romangulevatiy.emerald.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for Administration API endpoints")
@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class,
        UserPrincipalService.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class})
class AdminControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AdminService adminService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserPrincipalService userPrincipalService;

    @DisplayName("GET /api/admin/users should return 200 OK when request is valid")
    @WithMockUser(roles = "ADMIN")
    @Test
    void getUsers_ShouldReturn200_WhenRequestIsValid() throws Exception {
        UserResponse user = UserResponse.builder()
                .username("JustUser")
                .role(UserRole.USER)
                .build();
        UserResponse admin = UserResponse.builder()
                .username("SuperUser")
                .role(UserRole.ADMIN)
                .build();
        List<UserResponse> users = List.of(user, admin);

        PageResponse<UserResponse> pageResponse = PageResponse.<UserResponse>builder()
                .content(users)
                .totalElements(2)
                .build();

        when(adminService.getUsers(0, 16)).thenReturn(pageResponse);

        mockMvc.perform(get("/admin/users")
                        .param("page", "0")
                        .param("size", "16")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].username").value("JustUser"))
                .andExpect(jsonPath("$.content[1].username").value("SuperUser"));
    }

    @DisplayName("GET /api/admin/users should return 400 Bad Request when request is invalid")
    @WithMockUser(roles = "ADMIN")
    @Test
    void getUsers_ShouldReturn400_WhenRequestIsInvalid() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .param("page", "0")
                        .param("size", "67")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("GET /api/admin/users should return 403 Forbidden when user does not have permission")
    @WithMockUser(roles = "USER")
    @Test
    void getUsers_ShouldReturn403_WhenUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .param("page", "0")
                        .param("size", "16")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @DisplayName("PATCH /api/admin/users/{username} should return 200 OK when request is valid")
    @WithMockUser(roles = "ADMIN")
    @Test
    void updateUserRole_ShouldReturn200_WhenRequestIsValid() throws Exception {
        String username = "JustUser";

        mockMvc.perform(patch("/admin/users/{username}", username)
                        .param("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("PATCH /api/admin/users/{username} should return 403 Forbidden when user does not have permission")
    @WithMockUser(roles = "USER")
    @Test
    void updateUserRole_ShouldReturn403_WhenUserDoesNotHavePermission() throws Exception {
        mockMvc.perform(patch("/admin/users/SomeUser")
                        .param("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @DisplayName("PATCH /api/admin/users/{username} should return 404 Not Found when user does not exist")
    @WithMockUser(roles = "ADMIN")
    @Test
    void updateUserRole_ShouldReturn404_WhenUserDoesNotExist() throws Exception {
        String username = "NonExistentUser";

        doThrow(new EntityNotFoundException("User not found"))
                .when(adminService).updateUserRole(username, UserRole.ADMIN);

        mockMvc.perform(patch("/admin/users/{username}", username)
                        .param("role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}