package com.tarot.insight.domain.reservation.service;

import com.tarot.insight.domain.reader.entity.TarotReader;
import com.tarot.insight.domain.reader.repository.TarotReaderRepository;
import com.tarot.insight.domain.reservation.dto.ReservationRequest;
import com.tarot.insight.domain.reservation.entity.ConsultationReservation;
import com.tarot.insight.domain.reservation.entity.ReservationStatus;
import com.tarot.insight.domain.reservation.repository.ReservationRepository;
import com.tarot.insight.domain.user.User;
import com.tarot.insight.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}