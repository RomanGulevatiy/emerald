package me.romangulevatiy.emerald.service;

import me.romangulevatiy.emerald.dto.AuthRequest;
import me.romangulevatiy.emerald.dto.AuthResponse;
import me.romangulevatiy.emerald.dto.RefreshTokenRequest;

public interface AuthService {

    AuthResponse register(AuthRequest authRequest);

    AuthResponse login(AuthRequest authRequest);

    void logout(String authHeader, RefreshTokenRequest refreshTokenRequest);

    AuthResponse refresh(RefreshTokenRequest refreshTokenRequest);
}
