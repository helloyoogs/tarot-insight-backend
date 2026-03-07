package com.tarot.insight.domain.reservation.controller;

import com.tarot.insight.domain.reservation.dto.ReservationRequest;
import com.tarot.insight.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<String> createReservation(
            Authentication authentication, // JWT에서 유저 정보 가져옴
            @RequestBody ReservationRequest request) {

        String email = authentication.getName();
        Long reservationId = reservationService.createReservation(email, request);

        return ResponseEntity.ok("상담 예약이 성공적으로 완료되었습니다. 예약 번호: " + reservationId);
    }
}