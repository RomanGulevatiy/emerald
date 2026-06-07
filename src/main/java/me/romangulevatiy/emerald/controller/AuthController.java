package me.romangulevatiy.emerald.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.romangulevatiy.emerald.dto.AuthRequest;
import me.romangulevatiy.emerald.dto.AuthResponse;
import me.romangulevatiy.emerald.dto.RefreshTokenRequest;
import me.romangulevatiy.emerald.service.AuthService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
class AuthController {

    private final AuthService authService;

    public AuthResponse register(@RequestBody @Valid AuthRequest authRequest) {
        return authService.register(authRequest);
    }

    public AuthResponse login(@RequestBody @Valid AuthRequest authRequest) {
        return authService.login(authRequest);
    }

    public AuthResponse refresh(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return authService.refresh(refreshTokenRequest);
    }
}
