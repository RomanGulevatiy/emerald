package me.romangulevatiy.emerald.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.romangulevatiy.emerald.dto.AuthRequest;
import me.romangulevatiy.emerald.dto.AuthResponse;
import me.romangulevatiy.emerald.dto.RefreshTokenRequest;
import me.romangulevatiy.emerald.entity.UserEntity;
import me.romangulevatiy.emerald.exception.UsernameAlreadyExistsException;
import me.romangulevatiy.emerald.mapper.AuthMapper;
import me.romangulevatiy.emerald.repository.UserRepository;
import me.romangulevatiy.emerald.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public AuthResponse register(AuthRequest authRequest) {
        String username = authRequest.getUsername();

        if(userRepository.existsByUsername(username)) {
            log.error("Username @{} already exists", username);
            throw new UsernameAlreadyExistsException("Username @" + username + " already exists");
        }

        String encodedPassword = passwordEncoder.encode(authRequest.getPassword());
        UserEntity user = authMapper.toUserEntity(authRequest, encodedPassword);
        UserEntity savedUser = userRepository.save(user);

        log.info("Registering user @{}", savedUser.getUsername());
        return authMapper.toAuthResponse(savedUser);
    }

    @Transactional
    @Override
    public AuthResponse login(AuthRequest authRequest) {
        return null;
    }

    @Transactional
    @Override
    public AuthResponse refresh(RefreshTokenRequest refreshTokenRequest) {
        return null;
    }
}
