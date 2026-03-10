package com.tarot.insight.domain.user.service;

import com.tarot.insight.domain.user.entity.User;
import com.tarot.insight.domain.user.repository.UserRepository;
import com.tarot.insight.domain.user.entity.UserRole;
import com.tarot.insight.domain.user.dto.LoginRequest;
import com.tarot.insight.domain.user.dto.LoginResponse;
import com.tarot.insight.domain.user.dto.SignupRequest;
import com.tarot.insight.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate; // ✨ 추가
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit; // ✨ 추가

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate; // ✨ Redis 주입 추가

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // Access Token (30분 수명)
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());

        // Refresh Token (14일 수명)
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // 🛡️ [Redis 저장] Key: "RT:이메일", Value: "리프레시 토큰"
        // 14일 뒤에는 Redis에서도 자동으로 삭제되도록 설정.
        redisTemplate.opsForValue().set(
                "RT:" + user.getEmail(),
                refreshToken,
                14, // 이 숫자는 yml 설정과 맞추는 것이 좋습니다.
                TimeUnit.DAYS
        );

        // 발급된 2종 토큰을 응답 상자에 담아서 반환
        return new LoginResponse(accessToken, refreshToken);
    }
}