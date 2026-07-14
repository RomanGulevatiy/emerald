package me.romangulevatiy.emerald.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.romangulevatiy.emerald.service.TokenBlacklistService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void revoke(String jti, Date expiration) {
        long ttl = expiration.getTime() - System.currentTimeMillis();

        if(ttl <= 0) {
            log.warn("Expiration time {} is less than 0", expiration);
            return;
        }
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + jti, "revoked", ttl, TimeUnit.MILLISECONDS);
        log.info("Token revoked: {}", jti);
    }

    @Override
    public boolean isRevoked(String jti) {
        boolean isRevoked = redisTemplate.hasKey(BLACKLIST_PREFIX + jti);

        if(isRevoked) {
            log.info("Token is revoked: {}", jti);
        }
        return isRevoked;
    }
}
