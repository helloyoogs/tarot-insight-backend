package com.tarot.insight.global.error.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private final String code;    // 에러 코드 (예: RESERVATION_NOT_FOUND)
    private final String message; // 사용자에게 보여줄 메시지
}