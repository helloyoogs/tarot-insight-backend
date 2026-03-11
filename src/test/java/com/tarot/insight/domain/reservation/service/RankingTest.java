package com.tarot.insight.domain.reservation.service;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import com.tarot.insight.domain.reservation.dto.ReservationRequest;
import com.tarot.insight.domain.user.entity.User;
import com.tarot.insight.domain.user.entity.UserRole;
import com.tarot.insight.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class RankingTest {

    @Autowired private ReservationService reservationService;
    @Autowired private TarotReaderRepository tarotReaderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("상담사 5명을 생성하고 예약 횟수를 다르게 하여 랭킹 데이터를 만든다")
    void setupRankingData() {
        // 1. 유저 1명 생성 (예약자용)
        User buyer = User.builder()
                .email("buyer@test.com").nickname("예약자").password(passwordEncoder.encode("password")).role(UserRole.USER)
                .build();
        userRepository.save(buyer);

        // 2. 상담사 5명 생성 및 예약 건수 차등 부여
        // (닉네임, 예약 횟수 순)
        createReaderAndReservations("마스터 벨라", 10); // 1위 예상
        createReaderAndReservations("마스터 찰리", 7);  // 2위 예상
        createReaderAndReservations("마스터 데이빗", 5); // 3위 예상
        createReaderAndReservations("마스터 에바", 3);   // 4위 예상
        createReaderAndReservations("마스터 프랭크", 1); // 5위 예상

        System.out.println("✅ 테스트 데이터 생성 완료! 이제 Swagger에서 확인하세요.");
    }

    private void createReaderAndReservations(String nickname, int count) {
        // 상담사 유저 생성
        User readerUser = User.builder()
                .email(nickname + "@test.com").nickname(nickname).password(passwordEncoder.encode("password")).role(UserRole.READER)
                .build();
        userRepository.save(readerUser);

        // 상담사 프로필 생성
        TarotReader reader = TarotReader.builder()
                .user(readerUser).experienceYears(5).profile("베테랑 " + nickname).isActive(true).rating(4.5)
                .build();
        Long readerId = tarotReaderRepository.save(reader).getId();

        // 예약 생성 (점수 올리기 트리거 호출)
        for (int i = 0; i < count; i++) {
            ReservationRequest request = new ReservationRequest(readerId, "2026-03-10 1" + (i % 10) + ":00");
            try {
                reservationService.createReservation("buyer@test.com", request);
            } catch (Exception e) {
                // 시간 중복 에러는 무시 (테스트용이므로 점수만 올라가면 됨)
            }
        }
    }
}