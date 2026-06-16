package com.haichann.url_shortener.service;

import java.time.Duration;

public interface AccessTokenBlacklistService {
    void blacklistToken(String jti, Duration ttl);
    boolean isBlacklisted(String jti);
}
