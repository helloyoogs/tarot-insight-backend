package com.tarot.insight.global.error.exception;

import com.tarot.insight.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * ErrorCode Enum을 통째로 받는 생성자 (가장 권장되는 방식)
     * throw new BusinessException(ErrorCode.RESERVATION_ALREADY_EXISTS); 처럼 사용합니다.
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 부모 RuntimeException에 메시지 전달
        this.errorCode = errorCode;
    }

    /**
     * 상세 메시지를 직접 지정하고 싶을 때 사용하는 생성자
     */
    public BusinessException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}