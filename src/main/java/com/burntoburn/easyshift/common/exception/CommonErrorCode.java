package com.burntoburn.easyshift.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, 401, "유효하지 않은 입력값입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "접근이 거부되었습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, 404, "요청한 리소스를 찾을 수 없습니다."),

    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 내부 오류가 발생했습니다."),
    DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 501, "데이터베이스 오류가 발생했습니다."),
    TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, 504, "요청 시간이 초과되었습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}


