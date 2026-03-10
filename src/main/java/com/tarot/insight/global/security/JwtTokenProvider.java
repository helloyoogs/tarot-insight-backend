package com.tarot.insight.global.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationTime;

    // application.yml에 적어둔 비밀키와 만료 시간을 가져옵니다.
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-time}") long expirationTime) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }

    // 유저의 이메일(혹은 ID)과 권한을 넣어서 새로운 토큰을 만들어내는 메서드
    public String createToken(String email, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(email) // 토큰의 주인(주제)은 이메일
                .claim("role", role) // 토큰 안에 유저의 권한 정보도 슬쩍 넣어둠
                .issuedAt(now) // 발행 시간
                .expiration(validity) // 만료 시간
                .signWith(secretKey) // 서버의 비밀키로 도장 쾅! (위조 방지)
                .compact();
    }

    // 토큰에서 이메일 꺼내기 (토큰 해독)
    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰이 유효한지(위조/만료되지 않았는지) 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // 유효하지 않은 토큰이면 false를 반환하여 입장을 막습니다.
            return false;
        }
    }

    // 토큰의 남은 유효 시간(만료 시간 - 현재 시간)을 계산하여 반환합니다.
    public Long getExpiration(String token) {
        // 토큰을 해독하여 만료 날짜를 가져옵니다.
        Date expiration = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        // 현재 시간과의 차이를 밀리초(ms) 단위로 계산합니다.
        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}