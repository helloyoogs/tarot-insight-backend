package com.tarot.insight.domain.reservation.service;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import com.tarot.insight.domain.reader.service.TarotReaderRankingService;
import com.tarot.insight.domain.reservation.dto.ReservationRequest;
import com.tarot.insight.domain.reservation.dto.ReservationResponse;
import com.tarot.insight.domain.reservation.entity.ConsultationReservation;
import com.tarot.insight.domain.reservation.entity.ReservationStatus;
import com.tarot.insight.domain.reservation.repository.ReservationRepository;
import com.tarot.insight.domain.user.entity.User;
import com.tarot.insight.domain.user.repository.UserRepository;
import com.tarot.insight.global.error.ErrorCode;
import com.tarot.insight.global.error.exception.BusinessException;
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
    private final TarotReaderRankingService rankingService;

    @Transactional
    public Long createReservation(String email, ReservationRequest request) {
        // 1. 유저와 상담사 존재 확인 (BusinessException으로 통일)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        TarotReader reader = tarotReaderRepository.findById(request.getReaderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.READER_NOT_FOUND));

        // String 타입을 LocalDateTime으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime reservationTime = LocalDateTime.parse(request.getReservationTime(), formatter);

        // 2. 중복 예약 검증
        if (reservationRepository.existsByReaderAndReservationTimeAndStatus(
                reader, reservationTime, ReservationStatus.RESERVED)) {
            // 커스텀 에러 코드를 던집니다.
            throw new BusinessException(ErrorCode.RESERVATION_ALREADY_EXISTS);
        }

        // 3. 예약 생성 및 저장
        ConsultationReservation reservation = ConsultationReservation.builder()
                .user(user)
                .reader(reader)
                .reservationTime(reservationTime)
                .status(ReservationStatus.RESERVED)
                .build();

        Long reservationId = reservationRepository.save(reservation).getId();

        // [랭킹 업데이트]
        rankingService.incrementScore(reader.getUser().getNickname());

        return reservationId;
    }

    /**
     * 유저용: 내가 예약한 목록 전체 조회
     */
    @Transactional
    public List<ReservationResponse> getMyReservations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

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
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        TarotReader reader = tarotReaderRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.READER_NOT_FOUND));

        return reservationRepository.findAllByReaderIdOrderByReservationTimeAsc(reader.getId())
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }
}