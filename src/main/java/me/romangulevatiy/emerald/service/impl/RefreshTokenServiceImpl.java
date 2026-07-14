package me.romangulevatiy.emerald.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.romangulevatiy.emerald.exception.InvalidRefreshTokenException;
import me.romangulevatiy.emerald.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public String create(String username) {
        String rawToken = generateToken();
        String key = buildKey(rawToken);

        redisTemplate.opsForValue().set(key, username, refreshExpiration, TimeUnit.MILLISECONDS);
        log.info("Refresh token created for user: {}", username);

        return rawToken;
    }

    @Override
    public String extractUsername(String rawToken) {
        String key = buildKey(rawToken);
        String username = redisTemplate.opsForValue().get(key);

        if(username == null) {
            log.warn("Refresh token is invalid or expired");
            throw new InvalidRefreshTokenException("Refresh token is invalid or expired");
        }
        log.info("Extracted username {} from refresh token", username);
        return username;
    }

    @Override
    public void delete(String rawToken) {
        redisTemplate.delete(buildKey(rawToken));
    }

    /**
     * @return A securely generated random token encoded in URL-safe Base64 format without padding.
     */
    private String generateToken() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * @param rawToken The raw refresh token to be hashed and prefixed for storage in Redis.
     * @return A string key in the format "refresh:<hashedToken>" suitable for Redis storage.
     */
    private String buildKey(String rawToken) {
        String hashedToken = hashToken(rawToken);
        return REFRESH_TOKEN_PREFIX + hashedToken;
    }

    /**
     * @param rawToken The raw refresh token to be hashed using SHA-256.
     * @return A hexadecimal string representation of the SHA-256 hash of the raw token.
     * @throws RuntimeException if SHA-256 algorithm is not available.
     */
    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        }
        catch(NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
