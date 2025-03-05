package com.burntoburn.easyshift.exception.leave;

import com.burntoburn.easyshift.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LeaveErrorCode implements ErrorCode {
    LEAVE_NOT_FOUND(HttpStatus.NOT_FOUND, 4001, "해당 휴무신청을 찾을 수 없습니다."),
    DUPLICATE_LEAVE_REQUEST(HttpStatus.BAD_REQUEST, 4002, "해당 날짜에 이미 휴무신청이 존재합니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
