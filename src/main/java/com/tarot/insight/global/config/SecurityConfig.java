package com.tarot.insight.global.config;

import com.tarot.insight.global.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF: JWT 기반 API라 비활성
                .csrf(AbstractHttpConfigurer::disable)
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 세션/폼 로그인/HTTP Basic 미사용
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                // 인가 설정
                .authorizeHttpRequests(auth -> auth
                        // 1) 완전 공개 엔드포인트
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/reissue",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/ws-tarot/**"
                        ).permitAll()
                        // 채팅 기록 공개 정책: 필요에 따라 permitAll() 또는 hasRole("USER")로 변경
                        .requestMatchers("/api/chat/**").permitAll()

                        // 2) USER 권한 필요
                        .requestMatchers(
                                "/api/reservations",
                                "/api/reservations/my",
                                "/api/reservations/*/cancel",
                                "/api/reviews/**",
                                "/api/tarot/reading",
                                "/api/tarot/history",
                                "/api/tarot/themes/**",
                                "/api/auth/logout"
                        ).hasRole("USER")

                        // 3) READER 권한 필요
                        .requestMatchers(
                                "/api/reservations/schedule"
                        ).hasRole("READER")

                        // 4) ADMIN 권한 필요 (상담사 활성/비활성 제어)
                        .requestMatchers(
                                "/api/readers/*/activate",
                                "/api/readers/*/deactivate",
                                "/api/readers/admin"
                        ).hasRole("ADMIN")

                        // 5) 그 외 모든 요청은 인증만 필요 (ROLE 상관 X)
                        .anyRequest().authenticated()
                )
                // 예외 처리(필요하면 커스터마이징 가능)
                .exceptionHandling(ex -> {
                    // authenticationEntryPoint, accessDeniedHandler 추가 가능
                })
                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("*")); // 실제 배포 시 프론트 도메인으로 제한 권장
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}