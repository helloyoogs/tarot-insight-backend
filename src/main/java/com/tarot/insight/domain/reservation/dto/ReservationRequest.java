package com.tarot.insight.domain.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReservationRequest {
    private Long readerId;           // 누구에게?
    private LocalDateTime reservationTime; // 언제?
}