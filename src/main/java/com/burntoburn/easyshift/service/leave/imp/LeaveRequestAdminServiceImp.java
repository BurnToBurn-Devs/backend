package com.burntoburn.easyshift.service.leave.imp;

import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.exception.leave.LeaveException;
import com.burntoburn.easyshift.repository.leave.LeaveRequestRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import com.burntoburn.easyshift.service.leave.LeaveRequestAdminService;
import com.burntoburn.easyshift.service.leave.LeaveRequestFactory;
import java.time.YearMonth;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveRequestAdminServiceImp implements LeaveRequestAdminService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveRequestFactory leaveRequestFactory;


    @Override
    public LeaveRequest approveLeaveRequest(Long leaveRequestId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(LeaveException::leaveNotFound);
        leaveRequestFactory.approvedRequest(leaveRequest);

        return leaveRequest;
    }

    @Override
    public LeaveRequest rejectLeaveRequest(Long leaveRequestId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(LeaveException::leaveNotFound);
        leaveRequestFactory.rejectRequest(leaveRequest);
        return leaveRequest;
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByMonth(YearMonth scheduleMonth) {

        return leaveRequestRepository.findAllByScheduleMonth(scheduleMonth);
    }
}
