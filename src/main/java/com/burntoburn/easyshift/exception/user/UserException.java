package com.burntoburn.easyshift.exception.user;

import com.burntoburn.easyshift.common.exception.BusinessException;
import com.burntoburn.easyshift.common.exception.ErrorCode;

public class UserException extends BusinessException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static UserException userNotFound() {return new UserException(UserErrorCode.USER_NOT_FOUND);}

}