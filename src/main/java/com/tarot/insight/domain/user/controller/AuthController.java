package com.tarot.insight.domain.user.controller;

import com.tarot.insight.domain.user.dto.LoginRequest;
import com.tarot.insight.domain.user.dto.LoginResponse;
import com.tarot.insight.domain.user.dto.SignupRequest;
import com.tarot.insight.domain.user.service.UserService;
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

    // 회원가입 메서드
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        // 1. 서비스에 회원가입 로직을 위임합니다.
        userService.signup(request);

        // 2. 성공적으로 완료되면 상태 코드 201(CREATED)과 메시지를 반환합니다.
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

    // 로그인 메서드
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // 서비스에서 로그인을 처리하고 토큰이 담긴 응답을 받아옵니다.
        LoginResponse response = userService.login(request);

        // 상태 코드 200(OK)과 함께 토큰을 반환합니다.
        return ResponseEntity.ok(response);
    }
}