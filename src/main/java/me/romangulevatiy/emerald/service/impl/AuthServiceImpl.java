package me.romangulevatiy.emerald.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.romangulevatiy.emerald.dto.AuthRequest;
import me.romangulevatiy.emerald.dto.AuthResponse;
import me.romangulevatiy.emerald.dto.RefreshTokenRequest;
import me.romangulevatiy.emerald.entity.UserEntity;
import me.romangulevatiy.emerald.exception.InvalidCredentialsException;
import me.romangulevatiy.emerald.exception.InvalidRefreshTokenException;
import me.romangulevatiy.emerald.exception.UsernameAlreadyExistsException;
import me.romangulevatiy.emerald.mapper.AuthMapper;
import me.romangulevatiy.emerald.repository.UserRepository;
import me.romangulevatiy.emerald.security.JwtService;
import me.romangulevatiy.emerald.security.UserPrincipal;
import me.romangulevatiy.emerald.service.AuthService;
import me.romangulevatiy.emerald.service.RefreshTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    @Override
    public AuthResponse register(AuthRequest authRequest) {
        String requestUsername = authRequest.getUsername();

        if(userRepository.existsByUsername(requestUsername)) {
            log.error("Username @{} already exists", requestUsername);
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        String encodedPassword = passwordEncoder.encode(authRequest.getPassword());
        UserEntity user = authMapper.toUserEntity(authRequest, encodedPassword);

        UserEntity savedUser = userRepository.save(user);
        String savedUsername = savedUser.getUsername();
        log.info("User @{} registered successfully", savedUsername);

        UserPrincipal userPrincipal = new UserPrincipal(savedUser);
        Map<String,Object> extraClaims = createExtraClaims(savedUser);

        String accessToken = jwtService.generateAccessToken(extraClaims, userPrincipal);
        String refreshToken = refreshTokenService.create(savedUsername);

        return authMapper.toAuthResponse(accessToken, refreshToken, savedUsername);
    }

    @Transactional(readOnly = true)
    @Override
    public AuthResponse login(AuthRequest authRequest) {
        String requestUsername = authRequest.getUsername();

        UserEntity user = userRepository.findByUsername(requestUsername)
                .orElseThrow(() -> {
                    log.warn("Username @{} not found", requestUsername);
                    return new InvalidCredentialsException("Invalid username or password");
                });
        String username = user.getUsername();

        if(!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            log.error("Invalid password for user @{}", username);
            throw new InvalidCredentialsException("Invalid username or password");
        }

        UserPrincipal userPrincipal = new UserPrincipal(user);
        Map<String, Object> extraClaims = createExtraClaims(user);

        String accessToken = jwtService.generateAccessToken(extraClaims, userPrincipal);
        String refreshToken = refreshTokenService.create(username);

        log.info("User @{} logged in successfully", username);
        return authMapper.toAuthResponse(accessToken, refreshToken, username);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();
        String username = refreshTokenService.extractUsername(requestRefreshToken);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User @{} not found for refresh token", username);
                    return new InvalidRefreshTokenException("Refresh token is invalid or expired");
                });
        UserPrincipal userPrincipal = new UserPrincipal(user);

        Map<String, Object> extraClaims = createExtraClaims(user);
        String newAccessToken = jwtService.generateAccessToken(extraClaims, userPrincipal);

        refreshTokenService.delete(requestRefreshToken);
        String newRefreshToken = refreshTokenService.create(username);

        log.info("Access and Refresh tokens refreshed successfully for user @{}", username);
        return authMapper.toAuthResponse(newAccessToken, newRefreshToken, username);
    }

    private Map<String, Object> createExtraClaims(UserEntity user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("role", user.getRole().name());
        return extraClaims;
    }
}
