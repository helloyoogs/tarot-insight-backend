package com.tarot.insight.domain.reservation.service;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import com.tarot.insight.domain.reservation.dto.ReservationRequest;
import com.tarot.insight.domain.reservation.dto.ReservationResponse;
import com.tarot.insight.domain.reservation.entity.ConsultationReservation;
import com.tarot.insight.domain.reservation.entity.ReservationStatus;
import com.tarot.insight.domain.reservation.repository.ReservationRepository;
import com.tarot.insight.domain.user.User;
import com.tarot.insight.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TarotReaderRepository tarotReaderRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createReservation(String email, ReservationRequest request) {
        // 1. 유저와 상담사 존재 확인
        User user = userRepository.findByEmail(email).orElseThrow();
        TarotReader reader = tarotReaderRepository.findById(request.getReaderId()).orElseThrow();

        // 2. ✨ 중복 예약 검증
        if (reservationRepository.existsByReaderAndReservationTimeAndStatus(
                reader, request.getReservationTime(), ReservationStatus.RESERVED)) {
            throw new IllegalStateException("이미 예약된 시간입니다.");
        }

        // 3. 예약 생성 및 저장
        ConsultationReservation reservation = ConsultationReservation.builder()
                .user(user)
                .reader(reader)
                .reservationTime(request.getReservationTime())
                .status(ReservationStatus.RESERVED)
                .build();

        return reservationRepository.save(reservation).getId();
    }
    /**
     * 유저용: 내가 예약한 목록 전체 조회
     */
    @Transactional
    public List<ReservationResponse> getMyReservations(String email) {
        // 1. 이메일로 현재 로그인한 유저 객체 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 2. 레포지토리에서 유저 ID로 모든 예약 찾기 (최신순)
        // 3. 찾은 엔티티 리스트를 스트림으로 변환하여 DTO로 매핑
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
        // 1. 이메일로 유저를 찾고
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 2. 해당 유저와 연결된 상담사(TarotReader) 프로필 찾기
        TarotReader reader = tarotReaderRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("상담사 프로필이 존재하지 않습니다."));

        // 3. 상담사 ID로 예약 스케줄 가져오기 (시간순)
        return reservationRepository.findAllByReaderIdOrderByReservationTimeAsc(reader.getId())
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }
}