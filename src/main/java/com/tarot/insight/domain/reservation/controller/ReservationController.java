package com.tarot.insight.domain.reservation.controller;

import com.tarot.insight.domain.reservation.dto.ReservationRequest;
import com.tarot.insight.domain.reservation.dto.ReservationResponse;
import com.tarot.insight.domain.reservation.service.ReservationFacade; // [추가]
import com.tarot.insight.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationFacade reservationFacade; // 분산 락 적용을 위한 파사드 주입

    @PostMapping
    public ResponseEntity<String> createReservation(
            Authentication authentication,
            @Valid @RequestBody ReservationRequest request) {

        String email = authentication.getName();

        // reservationService 대신 reservationFacade를 호출합니다.
        // 이제 이 메서드는 락을 획득한 한 명만 안전하게 통과시킵니다.
        Long reservationId = reservationFacade.createReservation(email, request);

        return ResponseEntity.ok("상담 예약이 성공적으로 완료되었습니다. 예약 번호: " + reservationId);
    }

    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<String> cancelReservation(
            Authentication authentication,
            @PathVariable Long reservationId
    ) {
        reservationService.cancelReservation(authentication.getName(), reservationId);
        return ResponseEntity.ok("예약이 성공적으로 취소되었습니다.");
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(Authentication authentication) {
        return ResponseEntity.ok(reservationService.getMyReservations(authentication.getName()));
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<ReservationResponse>> getReaderSchedule(Authentication authentication) {
        return ResponseEntity.ok(reservationService.getReaderSchedule(authentication.getName()));
    }
}