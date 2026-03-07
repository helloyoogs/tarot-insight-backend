package com.tarot.insight.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 요청 헤더에서 "Bearer "로 시작하는 토큰을 꺼냅니다.
        String token = resolveToken(request);

        // 2. 토큰이 존재하고, 유효하다면(위조되지 않았다면)
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 3. 토큰에서 이메일을 꺼냅니다.
            String email = jwtTokenProvider.getEmail(token);

            // 4. 스프링 시큐리티에게 "이 사람은 인증된 사람이야!"라고 도장을 쾅 찍어줍니다.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. 다음 작업(컨트롤러 등)으로 넘겨줍니다.
        filterChain.doFilter(request, response);
    }

    // 헤더에서 순수 토큰만 쏙 빼내는 유틸리티 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " (7글자) 이후의 진짜 토큰만 반환
        }
        return null;
    }
}