package com.tarot.insight.global.security;

import com.tarot.insight.domain.reservation.controller.ReservationController;
import com.tarot.insight.domain.reservation.service.ReservationFacade;
import com.tarot.insight.domain.reservation.service.ReservationService;
import com.tarot.insight.global.config.SecurityConfig;
import com.tarot.insight.global.error.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.junit.jupiter.api.extension.ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = SecurityIntegrationMockMvcTest.TestConfig.class)
@TestPropertySource(properties = {
        "jwt.secret=your-very-long-and-secure-random-secret-key-for-tarot-insight",
        "jwt.access-token-expiration=1800000",
        "jwt.refresh-token-expiration=1209600000"
})
class SecurityIntegrationMockMvcTest {

    @Configuration
    @EnableWebMvc
    @Import({
            SecurityConfig.class,
            JwtTokenProvider.class,
            JwtAuthenticationFilter.class,
            GlobalExceptionHandler.class
    })
    static class TestConfig {
        @Bean
        ReservationService reservationService() {
            return Mockito.mock(ReservationService.class);
        }

        @Bean
        ReservationFacade reservationFacade() {
            return Mockito.mock(ReservationFacade.class);
        }

        @Bean
        StringRedisTemplate stringRedisTemplate() {
            return Mockito.mock(StringRedisTemplate.class);
        }

        @Bean
        ReservationController reservationController(ReservationService reservationService, ReservationFacade reservationFacade) {
            return new ReservationController(reservationService, reservationFacade);
        }
    }

    @org.springframework.beans.factory.annotation.Autowired
    private WebApplicationContext wac;

    @org.springframework.beans.factory.annotation.Autowired
    private JwtTokenProvider jwtTokenProvider;

    @org.springframework.beans.factory.annotation.Autowired
    private StringRedisTemplate redisTemplate;

    @org.springframework.beans.factory.annotation.Autowired
    private ReservationService reservationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Redis 블랙리스트에 없다고 가정(로그아웃 토큰 아님)
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> ops = (ValueOperations<String, String>) Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(ops);
        when(ops.get(anyString())).thenReturn(null);

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    private String bearer(String email, String role) {
        return "Bearer " + jwtTokenProvider.createAccessToken(email, role);
    }

    @Test
    @DisplayName("토큰 없이 보호 API 접근 시 403이어야 한다")
    void protectedApi_withoutToken_forbidden() throws Exception {
        mockMvc.perform(get("/api/reservations/my"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("USER 토큰 + DTO 검증 실패는 400 + C001 + errors를 반환해야 한다")
    void reservationCreate_userToken_validationError_returnsC001WithErrors() throws Exception {
        String invalidBody = """
                {
                  "readerId": 1,
                  "reservationTime": "2026/03/15 14:00"
                }
                """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearer("user@test.com", "USER"))
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C001"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").value("reservationTime"));
    }

    @Test
    @DisplayName("USER 토큰으로 READER 전용 API 접근 시 403이어야 한다")
    void readerSchedule_userToken_forbidden() throws Exception {
        mockMvc.perform(get("/api/reservations/schedule")
                        .header("Authorization", bearer("user@test.com", "USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("READER 토큰으로 스케줄 조회는 200이어야 한다")
    void readerSchedule_readerToken_ok() throws Exception {
        when(reservationService.getReaderSchedule(anyString())).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/reservations/schedule")
                        .header("Authorization", bearer("reader@test.com", "READER")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("깨진 JSON 요청은 400 + C001 + 고정 메시지를 반환해야 한다")
    void invalidJson_returnsC001WithMessage() throws Exception {
        String brokenJson = """
                {
                  "readerId": 1,
                  "reservationTime": "2026-03-15 14:00",
                }
                """;

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearer("user@test.com", "USER"))
                        .content(brokenJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C001"))
                .andExpect(jsonPath("$.message").value("요청 본문(JSON) 형식이 올바르지 않습니다."));
    }
}

