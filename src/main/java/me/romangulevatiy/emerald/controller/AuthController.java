package me.romangulevatiy.emerald.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.romangulevatiy.emerald.dto.AuthRequest;
import me.romangulevatiy.emerald.dto.AuthResponse;
import me.romangulevatiy.emerald.dto.RefreshTokenRequest;
import me.romangulevatiy.emerald.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token refresh")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user", description = "Registers a new user with the provided details")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody @Valid AuthRequest authRequest) {
        return authService.register(authRequest);
    }

    @Operation(summary = "Login a user", description = "Authenticates a user with the provided username and password")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@RequestBody @Valid AuthRequest authRequest) {
        return authService.login(authRequest);
    }

    @Operation(summary = "Logout a user", description = "Logs out a user by invalidating the provided refresh token")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        authService.logout(refreshTokenRequest);
    }

    @Operation(summary = "Refresh access token", description = "Issues a new access token using a valid refresh token")
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse refresh(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return authService.refresh(refreshTokenRequest);
    }
}
