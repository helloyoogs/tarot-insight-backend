package com.tarot.insight.global.error.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private final int status;     // HTTP 상태 코드 (예: 400, 404, 500)
    private final String code;    // 비즈니스 에러 코드 (예: RESERVATION_NOT_FOUND)
    private final String message; // 사용자에게 보여줄 메시지

    /**
     * 필드 단위 검증 에러 목록
     * - 민감정보/내부값 유출 방지를 위해 rejectedValue 같은 값은 포함하지 않습니다.
     */
    private final List<FieldError> errors;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class FieldError {
        private final String field;
        private final String reason;
    }
}