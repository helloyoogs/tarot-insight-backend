package com.tarot.insight.domain.user.controller;

import com.tarot.insight.domain.user.dto.LoginRequest;
import com.tarot.insight.domain.user.dto.LoginResponse;
import com.tarot.insight.domain.user.dto.SignupRequest;
import com.tarot.insight.domain.user.dto.TokenRequest; // ✨ 추가
import com.tarot.insight.domain.user.service.AuthService;
import com.tarot.insight.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7);
            authService.logout(accessToken);
            return ResponseEntity.ok("성공적으로 로그아웃되었습니다.");
        }
        return ResponseEntity.badRequest().body("잘못된 요청입니다.");
    }

    // 토큰 재발급 API
    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(@Valid @RequestBody TokenRequest tokenRequest) {
        // DTO에서 리프레시 토큰을 꺼내 서비스로 전달합니다.
        LoginResponse response = authService.reissue(tokenRequest.getRefreshToken());

        // 새로운 Access/Refresh 토큰 세트를 반환합니다.
        return ResponseEntity.ok(response);
    }
}