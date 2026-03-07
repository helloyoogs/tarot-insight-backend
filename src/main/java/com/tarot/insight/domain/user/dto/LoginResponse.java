package com.tarot.insight.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token; // 발급된 JWT 토큰을 담을 변수
}