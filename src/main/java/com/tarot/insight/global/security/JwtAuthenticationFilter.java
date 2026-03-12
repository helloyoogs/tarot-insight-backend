package com.tarot.insight.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 요청 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰이 존재하고 기본 유효성 검사(위조/만료) 통과 시
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 로그아웃 시 Redis에 토큰을 Key로 저장했으므로, 값이 존재하면 "이미 로그아웃된 토큰"입니다.
            String isLogout = redisTemplate.opsForValue().get(token);

            if (isLogout == null) { // 블랙리스트에 없을 때만 인증 처리 진행
                // 3. 토큰에서 사용자 정보 추출
                String email = jwtTokenProvider.getEmail(token);

                // 토큰에서 role 꺼내서 권한으로 변환
                String role = jwtTokenProvider.getRole(token); // "USER" / "READER" / null
                List<GrantedAuthority> authorities = Collections.emptyList();
                if (role != null) {
                    // hasRole("USER") → 내부적으로 "ROLE_USER"를 요구하므로 prefix 붙여줌
                    authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                }

                // 4. 스프링 시큐리티 인증 정보 설정 (authorities 반드시 전달 → hasRole("USER") 통과)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.warn("🚨 로그아웃된 토큰으로 접근 시도 차단: [{}]", token);
                // SecurityContext를 설정하지 않고 넘어가므로, 이후 권한이 필요한 API 접근 시 자동으로 401/403 에러가 발생.
            }
        }

        // 5. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}