package com.tarot.insight.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // SecurityConfig에서 허락해주지 않았으므로, 반드시 토큰이 있어야만 들어올 수 있음.
    @GetMapping("/me")
    public ResponseEntity<String> getMyInfo(Authentication authentication) {
        // 필터가 검사를 마치고 authentication 객체 안에 이메일을 담기.
        String email = authentication.getName();
        return ResponseEntity.ok("환영합니다! 인증된 사용자의 이메일은 [" + email + "] 입니다.");
    }
}