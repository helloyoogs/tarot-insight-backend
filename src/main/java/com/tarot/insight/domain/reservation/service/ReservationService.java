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

        // 취소된 예약은 스케줄에서 제외 (실제 상담이 예정된 슬롯만 노출)
        return reservationRepository.findAllByReaderIdOrderByReservationTimeAsc(reader.getId())
                .stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.RESERVED)
                .map(ReservationResponse::new)
                .toList();
    }

    /**
     * 유저용: 예약 취소 (상담 24시간 전까지만 가능)
     */
    @Transactional
    public void cancelReservation(String email, Long reservationId) {
        // 1. 유저 인증
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        // 2. 예약 조회
        ConsultationReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        // 3. 본인 소유 예약인지 검증
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 4. 이미 완료되었거나 취소된 예약은 취소 불가
        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new BusinessException(ErrorCode.RESERVATION_CANCEL_NOT_ALLOWED);
        }

        // 5. 24시간 이내인 경우 취소 불가
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = reservation.getReservationTime().minusHours(24);
        if (!now.isBefore(limit)) { // now >= reservationTime - 24h 이면 취소 불가
            throw new BusinessException(ErrorCode.RESERVATION_CANCEL_NOT_ALLOWED);
        }

        // 6. 상태 변경 (CANCELLED)
        reservation.cancel();
    }
}