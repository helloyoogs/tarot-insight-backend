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
    private final long accessTokenExpiration;  // Access Token 만료 시간
    private final long refreshTokenExpiration; // Refresh Token 만료 시간

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // 1. Access Token 생성 (30분 등 짧은 수명)
    public String createAccessToken(String email, String role) {
        return createToken(email, role, accessTokenExpiration);
    }

    // 2. Refresh Token 생성 (2주 등 긴 수명)
    public String createRefreshToken(String email) {
        // Refresh Token은 보안상 권한 정보(role)를 넣지 않는 것이 일반적입니다.
        return createToken(email, null, refreshTokenExpiration);
    }

    // 3. 공통 토큰 생성 로직 (내부에서만 사용)
    private String createToken(String email, String role, long expiration) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expiration);

        var builder = Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey);

        // 권한 정보가 있는 경우에만 claim에 추가 (Access Token용)
        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    // 토큰에서 이메일 꺼내기
    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰 남은 시간 계산
    public Long getExpiration(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    // 토큰에서 role 꺼내기
    public String getRole(String token) {
        var claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Object role = claims.get("role");
        return role != null ? role.toString() : null;
    }
}