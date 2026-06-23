package me.romangulevatiy.emerald.service.impl;

import me.romangulevatiy.emerald.dto.PageResponse;
import me.romangulevatiy.emerald.dto.UserResponse;
import me.romangulevatiy.emerald.entity.UserEntity;
import me.romangulevatiy.emerald.entity.enums.UserRole;
import me.romangulevatiy.emerald.exception.EntityNotFoundException;
import me.romangulevatiy.emerald.mapper.UserMapper;
import me.romangulevatiy.emerald.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Unit tests for Administration Service implementation")
@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @InjectMocks private AdminServiceImpl adminService;

    @DisplayName("getUsers should return a PageResponse containing users when users exist")
    @Test
    void getUsers_ShouldReturnPageResponse_WhenUsersExist() {
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        UserResponse userResponse = new UserResponse();
        UserResponse userResponse2 = new UserResponse();

        List<UserResponse> users = List.of(userResponse, userResponse2);
        Page<UserResponse> userPage = new PageImpl<>(users, pageRequest, users.size());
        PageResponse<UserResponse> pageResponse = PageResponse.<UserResponse>builder()
                .totalElements(userPage.getTotalElements())
                .build();

        when(userRepository.findAllUsers(pageRequest)).thenReturn(userPage);
        when(userMapper.toPageResponse(userPage)).thenReturn(pageResponse);

        PageResponse<UserResponse> result = adminService.getUsers(page, size);

        assertNotNull(result);
        assertEquals(pageResponse, result);
        assertEquals(2, result.getTotalElements());
        verify(userRepository).findAllUsers(pageRequest);
        verify(userMapper).toPageResponse(userPage);
    }

    @DisplayName("getUsers should return an empty PageResponse when no users exist")
    @Test
    void getUsers_ShouldReturnEmptyPageResponse_WhenNoUsersExist() {
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<UserResponse> users = List.of();
        Page<UserResponse> userPage = new PageImpl<>(users, pageRequest, 0);
        PageResponse<UserResponse> pageResponse = new PageResponse<>();

        when(userRepository.findAllUsers(pageRequest)).thenReturn(userPage);
        when(userMapper.toPageResponse(userPage)).thenReturn(pageResponse);

        PageResponse<UserResponse> result = adminService.getUsers(page, size);

        assertNotNull(result);
        assertEquals(pageResponse, result);
        assertEquals(0, result.getTotalElements());
        verify(userRepository).findAllUsers(pageRequest);
        verify(userMapper).toPageResponse(userPage);
    }

    @DisplayName("updateUserRole should update the user's role when the user exists")
    @Test
    void updateUserRole_ShouldUpdateUserRole_WhenUserExists() {
        String username = "SuperUser";
        UserRole newRole = UserRole.ADMIN;

        UserEntity user = UserEntity.builder()
                .username(username)
                .role(UserRole.USER)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.ofNullable(user));

        adminService.updateUserRole(username, newRole);

        assertNotNull(user);
        assertEquals(UserRole.ADMIN, user.getRole());
        verify(userRepository).findByUsername(username);
    }

    @DisplayName("updateUserRole should throw EntityNotFoundException when the user does not exist")
    @Test
    void updateUserRole_ShouldThrowEntityNotFoundException_WhenUserDoesNotExist() {
        String username = "SuperUser";
        UserRole newRole = UserRole.USER;

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminService.updateUserRole(username, newRole));
        verify(userRepository).findByUsername(username);
    }
}