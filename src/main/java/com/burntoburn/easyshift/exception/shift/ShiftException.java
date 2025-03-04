package com.burntoburn.easyshift.exception.shift;


import static com.burntoburn.easyshift.exception.shift.ShiftErrorCode.SHIFT_NOT_FOUND;

import com.burntoburn.easyshift.common.exception.BusinessException;
import com.burntoburn.easyshift.common.exception.ErrorCode;

public class ShiftException extends BusinessException {

    public ShiftException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static ShiftException shiftNotFound() {
        return new ShiftException(SHIFT_NOT_FOUND);
    }

}
