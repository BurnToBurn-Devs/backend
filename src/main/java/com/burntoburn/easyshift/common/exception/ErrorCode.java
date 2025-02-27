package com.burntoburn.easyshift.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getHttpStatus();

    int getCode();

    String getMessage();
}
