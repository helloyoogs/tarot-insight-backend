package com.tarot.insight.domain.user.service;

import com.tarot.insight.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    public void logout(String accessToken) {
        // 1. 토큰에서 남은 유효 시간 추출 (아까 만든 getExpiration 사용)
        Long expiration = jwtTokenProvider.getExpiration(accessToken);

        // 2. Redis 블랙리스트에 저장
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }
}