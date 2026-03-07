package com.tarot.insight.domain.user.service;

import com.tarot.insight.domain.user.User;
import com.tarot.insight.domain.user.UserRepository;
import com.tarot.insight.domain.user.UserRole;
import com.tarot.insight.domain.user.dto.LoginRequest;
import com.tarot.insight.domain.user.dto.LoginResponse;
import com.tarot.insight.domain.user.dto.SignupRequest;
import com.tarot.insight.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입 로직
    @Transactional
    public Long signup(SignupRequest request) {
        // 1. 이메일 중복 확인 (이미 있으면 에러 발생)
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. 비밀번호 암호화 (SecurityConfig에서 만든 도구 사용)
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 저장할 User 객체 조립 (Builder 패턴 활용)
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .role(UserRole.USER) // 가입 시 기본 권한은 일반 사용자(USER)
                .build();

        // 4. DB에 저장하고, 저장된 유저의 ID 번호를 반환
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    //로그인 로직
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // 1. 이메일로 유저 찾기 (없으면 에러 발생)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 2. 비밀번호 일치하는지 확인 (평문 비밀번호와 암호화된 비밀번호 비교)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 비밀번호가 맞다면 토큰 기계를 작동시켜 토큰 발급!
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());

        // 4. 발급된 토큰을 응답 상자에 담아서 반환
        return new LoginResponse(token);
    }
}