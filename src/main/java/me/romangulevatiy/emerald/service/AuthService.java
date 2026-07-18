package me.romangulevatiy.emerald.service;

import me.romangulevatiy.emerald.dto.request.AuthRequest;
import me.romangulevatiy.emerald.dto.response.AuthResponse;
import me.romangulevatiy.emerald.dto.request.RefreshTokenRequest;

public interface AuthService {

    AuthResponse register(AuthRequest authRequest);

    AuthResponse login(AuthRequest authRequest);

    void logout(String authHeader, RefreshTokenRequest refreshTokenRequest);

    AuthResponse refresh(RefreshTokenRequest refreshTokenRequest);
}
