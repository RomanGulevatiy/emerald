package me.romangulevatiy.emerald.service.impl;

import lombok.RequiredArgsConstructor;
import me.romangulevatiy.emerald.dto.AuthRequest;
import me.romangulevatiy.emerald.dto.AuthResponse;
import me.romangulevatiy.emerald.dto.RefreshTokenRequest;
import me.romangulevatiy.emerald.repository.UserRepository;
import me.romangulevatiy.emerald.service.AuthService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public AuthResponse register(AuthRequest authRequest) {
        return null;
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        return null;
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest refreshTokenRequest) {
        return null;
    }
}
