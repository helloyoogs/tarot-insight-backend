package com.tarot.insight.global.error.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private final int status;     // HTTP 상태 코드 (예: 400, 404, 500)
    private final String code;    // 비즈니스 에러 코드 (예: RESERVATION_NOT_FOUND)
    private final String message; // 사용자에게 보여줄 메시지
}