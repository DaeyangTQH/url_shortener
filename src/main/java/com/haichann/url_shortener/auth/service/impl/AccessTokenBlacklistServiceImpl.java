package com.haichann.url_shortener.auth.service.impl;

import com.haichann.url_shortener.auth.service.AccessTokenBlacklistService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccessTokenBlacklistServiceImpl implements AccessTokenBlacklistService {
    static String PREFIX = "blacklist:access_token:";

    StringRedisTemplate stringRedisTemplate;

    @Override
    public void blacklistToken(String jti, Duration ttl) {
        if (jti == null || jti.isBlank() || ttl == null || ttl.isZero() || ttl.isNegative()) {
            return;
        }
        stringRedisTemplate.opsForValue().set(PREFIX + jti, "revoked", ttl);
    }

    @Override
    public boolean isBlacklisted(String jti) {
        if (jti == null || jti.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(PREFIX + jti));
    }
}
