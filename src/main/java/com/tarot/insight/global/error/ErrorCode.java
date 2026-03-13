package com.tarot.insight.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // --- 공통 에러 (C) ---
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부 오류가 발생했습니다."),

    // --- 보안/인증 에러 (A) ---
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "로그인이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A003", "이메일 또는 비밀번호가 일치하지 않습니다."),

    // --- 예약 관련 에러 (R) ---
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "예약 내역을 찾을 수 없습니다."),
    RESERVATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "R002", "해당 시간은 이미 예약이 완료되었습니다."),
    RESERVATION_CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "R003", "예약은 상담 시작 24시간 전까지만 취소할 수 있습니다."),

    // --- 상담사 관련 에러 (D) ---
    READER_NOT_FOUND(HttpStatus.NOT_FOUND, "D001", "해당 상담사를 찾을 수 없습니다."),
    READER_INACTIVE(HttpStatus.BAD_REQUEST, "D002", "현재 상담을 받을 수 없는 상담사입니다."),

    // --- 데이터/콘텐츠 관련 에러 (T) ---
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "요청하신 타로 콘텐츠를 찾을 수 없습니다.");

    private final HttpStatus status; // HTTP 상태 코드 (예: 400, 404, 500)
    private final String code;       // 우리가 정한 고유 코드 (예: R002)
    private final String message;    // 사용자에게 보여줄 친절한 설명
}