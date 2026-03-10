package com.tarot.insight.domain.user.service;

import com.tarot.insight.domain.user.dto.LoginResponse;
import com.tarot.insight.domain.user.entity.User;
import com.tarot.insight.domain.user.repository.UserRepository;
import com.tarot.insight.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository; // ✨ 유저 권한 조회를 위해 추가!

    // [기능 1] 로그아웃 (블랙리스트 처리)
    public void logout(String accessToken) {
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    // [기능 2] 토큰 재발급 (Refresh Token Rotation)
    @Transactional
    public LoginResponse reissue(String refreshToken) {
        // 1. Refresh Token 자체가 유효한지 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 리프레시 토큰입니다.");
        }

        // 2. 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmail(refreshToken);

        // 3. Redis에 저장된 해당 유저의 Refresh Token이 맞는지 확인
        String savedRefreshToken = redisTemplate.opsForValue().get("RT:" + email);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("로그인 정보가 일치하지 않습니다. 다시 로그인해주세요.");
        }

        // 4. 새로운 토큰 세트(Access & Refresh) 생성
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtTokenProvider.createAccessToken(email, user.getRole().name());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

        // 5. Redis에 새로운 Refresh Token으로 교체
        redisTemplate.opsForValue().set(
                "RT:" + email,
                newRefreshToken,
                14, // 14일
                TimeUnit.DAYS
        );

        return new LoginResponse(newAccessToken, newRefreshToken);
    }
}