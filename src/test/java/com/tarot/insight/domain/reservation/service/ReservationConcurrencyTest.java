package com.tarot.insight.domain.reservation.service;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import com.tarot.insight.domain.reservation.dto.ReservationRequest;
import com.tarot.insight.domain.reservation.repository.ReservationRepository;
import com.tarot.insight.domain.review.repository.ReviewRepository; // 외래키 해결용 추가
import com.tarot.insight.domain.user.entity.User;
import com.tarot.insight.domain.user.repository.UserRepository;
import com.tarot.insight.domain.user.entity.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationConcurrencyTest {

    @Autowired private ReservationFacade reservationFacade;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private TarotReaderRepository tarotReaderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ReviewRepository reviewRepository; // [추가] 삭제 시 외래키 제약조건 해결용

    private Long targetReaderId; // DB에서 생성된 ID를 동적으로 보관
    private final List<String> testUserEmails = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 1. 테스트용 상담사 생성
        User readerUser = User.builder()
                .email("reader@test.com")
                .nickname("마스터 앨리스")
                .password("password")
                .role(UserRole.READER)
                .build();
        userRepository.save(readerUser);

        TarotReader reader = TarotReader.builder()
                .user(readerUser)
                .experienceYears(15)
                .profile("베테랑 상담사")
                .isActive(true)
                .rating(0.0)
                .build();

        // [핵심] DB에 저장된 실제 ID를 가져와서 필드에 저장 (데이터 무결성 확보)
        targetReaderId = tarotReaderRepository.save(reader).getId();
        System.out.println(">>>> [테스트 시작] 대상 상담사 ID: " + targetReaderId);

        // 2. 테스트용 유저 100명 생성
        for (int i = 0; i < 100; i++) {
            String email = "user" + i + "@test.com";
            User user = User.builder()
                    .email(email)
                    .nickname("유저" + i)
                    .password("password")
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
            testUserEmails.add(email);
        }
    }

    @AfterEach
    void tearDown() {
        // [중요] 삭제 순서: 리뷰(자식) -> 예약(부모) -> 상담사 -> 유저
        // 이 순서를 지켜야 SQLIntegrityConstraintViolationException이 발생하지 않습니다.
        reviewRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
        tarotReaderRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("동시에 100명이 예약 시도 시 Redisson 분산 락으로 인해 단 1명만 성공해야 한다")
    void concurrencyTest() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        String reservationTime = "2026-03-08 23:00";

        // [수정] setUp에서 저장한 targetReaderId를 사용하여 요청 객체 생성
        ReservationRequest request = new ReservationRequest(targetReaderId, reservationTime);

        // [실행] 100명이 동시에 예약 요청
        for (int i = 0; i < threadCount; i++) {
            String email = testUserEmails.get(i);
            executorService.submit(() -> {
                try {
                    reservationFacade.createReservation(email, request);
                } catch (Exception e) {
                    // 실패 로그 (락을 얻지 못하거나 이미 예약된 경우)
                    // System.out.println(">>>> 예약 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // [검증]
        long successCount = reservationRepository.count();
        System.out.println(">>>> 최종 예약 성공 건수: " + successCount);

        // 분산 락이 정상 작동한다면 무조건 1이어야 합니다.
        assertThat(successCount).isEqualTo(1);
    }
}