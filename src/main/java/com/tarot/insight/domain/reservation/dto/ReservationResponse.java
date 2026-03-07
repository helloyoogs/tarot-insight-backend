package com.tarot.insight.domain.reservation.dto;

import com.tarot.insight.domain.reservation.entity.ConsultationReservation;
import com.tarot.insight.domain.reservation.entity.ReservationStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationResponse {
    private Long id;
    private String userName;        // 유저 닉네임
    private String readerName;      // 상담사 닉네임
    private LocalDateTime reservationTime;
    private ReservationStatus status;

    public ReservationResponse(ConsultationReservation reservation) {
        this.id = reservation.getId();
        this.userName = reservation.getUser().getNickname();
        this.readerName = reservation.getReader().getUser().getNickname(); // 상담사의 유저 닉네임
        this.reservationTime = reservation.getReservationTime();
        this.status = reservation.getStatus();
    }
}