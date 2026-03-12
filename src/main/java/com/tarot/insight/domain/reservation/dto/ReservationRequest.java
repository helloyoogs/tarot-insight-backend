package com.tarot.insight.domain.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor  // 파라미터 없는 기본 생성자 (JSON 파싱용)
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 (테스트 코드에서 사용)
public class ReservationRequest {
    @NotNull(message = "상담사 ID는 필수입니다.")
    private Long readerId;

    @NotBlank(message = "예약 시간은 필수입니다.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$", message = "예약 시간 형식이 올바르지 않습니다. (예: 2026-03-15 14:00)")
    private String reservationTime;
}