package com.burntoburn.easyshift.service.leave;

import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import java.time.LocalDate;
import java.time.YearMonth;

import java.util.List;

// 관리자 기능
public interface LeaveRequestAdminService {

    // 휴무 승인
    LeaveRequest approveLeaveRequest(Long leaveRequestId);

    // 휴무 거절
    LeaveRequest rejectLeaveRequest(Long leaveRequestId);

    // 한 달 단위로 휴뮤 조회
    List<LeaveRequest> getLeaveRequestsByMonth(YearMonth scheduleMonth);

}
