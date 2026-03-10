package com.tarot.insight.domain.reservation.service;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import com.tarot.insight.domain.reader.service.TarotReaderRankingService; // [추가] 랭킹 서비스 임포트
import com.tarot.insight.domain.reservation.dto.ReservationRequest;
import com.tarot.insight.domain.reservation.dto.ReservationResponse;
import com.tarot.insight.domain.reservation.entity.ConsultationReservation;
import com.tarot.insight.domain.reservation.entity.ReservationStatus;
import com.tarot.insight.domain.reservation.repository.ReservationRepository;
import com.tarot.insight.domain.user.entity.User;
import com.tarot.insight.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TarotReaderRepository tarotReaderRepository;
    private final UserRepository userRepository;
    private final TarotReaderRankingService rankingService; // [추가] 랭킹 서비스 주입

    @Transactional
    public Long createReservation(String email, ReservationRequest request) {
        // 1. 유저와 상담사 존재 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        TarotReader reader = tarotReaderRepository.findById(request.getReaderId())
                .orElseThrow(() -> new IllegalArgumentException("상담사를 찾을 수 없습니다."));

        // String 타입을 LocalDateTime으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime reservationTime = LocalDateTime.parse(request.getReservationTime(), formatter);

        // 2. 중복 예약 검증
        if (reservationRepository.existsByReaderAndReservationTimeAndStatus(
                reader, reservationTime, ReservationStatus.RESERVED)) {
            throw new IllegalStateException("이미 예약된 시간입니다.");
        }

        // 3. 예약 생성 및 저장
        ConsultationReservation reservation = ConsultationReservation.builder()
                .user(user)
                .reader(reader)
                .reservationTime(reservationTime)
                .status(ReservationStatus.RESERVED)
                .build();

        Long reservationId = reservationRepository.save(reservation).getId();

        // [랭킹 업데이트] 예약이 성공적으로 완료된 시점에 상담사의 인기 점수를 1점 올립니다.
        // 닉네임을 기준으로 Redis ZSet에 저장됩니다.
        rankingService.incrementScore(reader.getUser().getNickname());

        return reservationId;
    }

    /**
     * 유저용: 내가 예약한 목록 전체 조회
     */
    @Transactional
    public List<ReservationResponse> getMyReservations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        return reservationRepository.findAllByUserIdOrderByReservationTimeDesc(user.getId())
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }

    /**
     * 상담사용: 나에게 들어온 예약 스케줄 조회
     */
    @Transactional
    public List<ReservationResponse> getReaderSchedule(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        TarotReader reader = tarotReaderRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("상담사 프로필이 존재하지 않습니다."));

        return reservationRepository.findAllByReaderIdOrderByReservationTimeAsc(reader.getId())
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }
}