package me.romangulevatiy.emerald.service.impl;

import me.romangulevatiy.emerald.dto.request.UpdatePasswordRequest;
import me.romangulevatiy.emerald.entity.UserEntity;
import me.romangulevatiy.emerald.entity.enums.UserRole;
import me.romangulevatiy.emerald.exception.EntityNotFoundException;
import me.romangulevatiy.emerald.exception.InvalidCredentialsException;
import me.romangulevatiy.emerald.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@DisplayName("Unit tests for User Service implementation")
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserServiceImpl userService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder()
                .id(1L)
                .username("SuperUser")
                .password("EncodedCurrentPassword")
                .role(UserRole.USER)
                .build();
    }

    @DisplayName("updatePassword should change password when current password is valid")
    @Test
    void updatePassword_ShouldChangePassword_WhenCurrentPasswordIsValid() {
        String encodedCurrentPassword = user.getPassword();
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("CurrentPassword")
                .newPassword("NewPassword")
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), encodedCurrentPassword)).thenReturn(true);
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("EncodedNewPassword");

        userService.updatePassword(user.getUsername(), request);

        assertEquals("EncodedNewPassword", user.getPassword());
        verify(userRepository).findByUsername(user.getUsername());
        verify(passwordEncoder).matches(request.getCurrentPassword(), encodedCurrentPassword);
        verify(passwordEncoder).encode(request.getNewPassword());
    }

    @DisplayName("updatePassword should throw EntityNotFoundException when user is missing")
    @Test
    void updatePassword_ShouldThrowEntityNotFoundException_WhenUserIsMissing() {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("CurrentPassword")
                .newPassword("NewPassword")
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updatePassword(user.getUsername(), request));
        verify(userRepository).findByUsername(user.getUsername());
        verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("updatePassword should throw InvalidCredentialsException when current password is invalid")
    @Test
    void updatePassword_ShouldThrowInvalidCredentialsException_WhenCurrentPasswordIsInvalid() {
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .currentPassword("WrongPassword")
                .newPassword("NewPassword")
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.updatePassword(user.getUsername(), request));
        assertEquals("EncodedCurrentPassword", user.getPassword());
        verify(userRepository).findByUsername(user.getUsername());
        verify(passwordEncoder).matches(request.getCurrentPassword(), user.getPassword());
        verify(passwordEncoder, never()).encode(request.getNewPassword());
    }
}