package com.tarot.insight.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor  // 파라미터 없는 기본 생성자 (JSON 파싱용)
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 (테스트 코드에서 사용)
public class ReservationRequest {
    private Long readerId;
    private String reservationTime;
}