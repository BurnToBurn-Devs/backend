package com.burntoburn.easyshift.exception.leave;

import com.burntoburn.easyshift.common.exception.BusinessException;
import com.burntoburn.easyshift.common.exception.ErrorCode;


public class LeaveException extends BusinessException {

    public LeaveException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static LeaveException leaveNotFound() {
        return new LeaveException(LeaveErrorCode.LEAVE_NOT_FOUND);
    }

    public static LeaveException leaveDuplicated(){
        return new LeaveException(LeaveErrorCode.DUPLICATE_LEAVE_REQUEST);
    }
}
