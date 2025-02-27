package com.burntoburn.easyshift.exception.schedule;

import com.burntoburn.easyshift.common.exception.BusinessException;
import com.burntoburn.easyshift.common.exception.ErrorCode;

import static com.burntoburn.easyshift.exception.schedule.ScheduleErrorCode.INSUFFICIENT_USERS_FOR_ASSIGNMENT;

public class ScheduleException extends BusinessException {

    public ScheduleException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static ScheduleException scheduleNotFound() {
        return new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND);
    }

    public static ScheduleException insufficientUsersForAssignment() {
        return new ScheduleException(INSUFFICIENT_USERS_FOR_ASSIGNMENT);
    }
}
