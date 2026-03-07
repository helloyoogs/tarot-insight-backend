package com.tarot.insight.domain.reservation.repository;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reservation.entity.ConsultationReservation;
import com.tarot.insight.domain.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ConsultationReservation, Long> {
    // 특정 상담사에게 같은 시간, '예약됨' 상태의 데이터가 있는지 확인하는 쿼리
    boolean existsByReaderAndReservationTimeAndStatus(
            TarotReader reader,
            LocalDateTime reservationTime,
            ReservationStatus status
    );
    // 유저별 예약 목록 (최신순)
    List<ConsultationReservation> findAllByUserIdOrderByReservationTimeDesc(Long userId);
    // 상담사별 예약 목록 (시간순)
    List<ConsultationReservation> findAllByReaderIdOrderByReservationTimeAsc(Long readerId);
}