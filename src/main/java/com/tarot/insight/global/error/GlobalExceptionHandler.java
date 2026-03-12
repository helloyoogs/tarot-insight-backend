package com.tarot.insight.global.error;

import com.tarot.insight.global.error.dto.ErrorResponse;
import com.tarot.insight.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 1. 우리가 정의한 비즈니스 예외(BusinessException) 처리
     * throw new BusinessException(ErrorCode.RESERVATION_ALREADY_EXISTS)가 발생하면 여기로 옵니다.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());

        // 예외 객체 안에 담긴 ErrorCode를 꺼내옵니다.
        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse response = ErrorResponse.builder()
                .status(errorCode.getStatus().value()) // Enum에 정의된 400, 404 등의 값
                .code(errorCode.getCode())             // "R002", "D001" 등 우리가 정한 코드
                .message(e.getMessage())               // "이미 예약된 시간입니다." 등의 메시지
                .errors(null)
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    /**
     * 2. 자바/스프링 표준 예외인 IllegalArgumentException 처리
     * 주로 잘못된 입력값이 들어왔을 때 발생합니다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException: {}", e.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus().value())
                .code(ErrorCode.INVALID_INPUT_VALUE.getCode())
                .message(e.getMessage())
                .errors(null)
                .build();

        return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getStatus()).body(response);
    }

    /**
     * 2-1. @Valid 검증 실패 처리 (DTO Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ErrorResponse.FieldError> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> ErrorResponse.FieldError.builder()
                        .field(err.getField())
                        .reason(err.getDefaultMessage())
                        .build())
                .toList();

        String message = errors.stream()
                .map(err -> err.getField() + ": " + err.getReason())
                .collect(Collectors.joining(", "));

        log.error("MethodArgumentNotValidException: {}", message);

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse response = ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message(message == null || message.isBlank() ? errorCode.getMessage() : message)
                .errors(errors.isEmpty() ? null : errors)
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    /**
     * 2-2. JSON 파싱 실패 처리 (잘못된 JSON/타입 불일치 등)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException: {}", e.getMessage());

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse response = ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message("요청 본문(JSON) 형식이 올바르지 않습니다.")
                .errors(null)
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    /**
     * 3. 그 외 예상치 못한 모든 최상위 에러 처리 (방어막)
     * 시스템에서 놓친 런타임 에러들이 마지막으로 여기에 걸립니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse response = ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(null)
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
}