package com.burntoburn.easyshift.service.leave.imp;

import com.burntoburn.easyshift.dto.leave.req.LeaveRequestDto;
import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.exception.leave.LeaveException;
import com.burntoburn.easyshift.exception.schedule.ScheduleException;
import com.burntoburn.easyshift.exception.user.UserException;
import com.burntoburn.easyshift.repository.leave.LeaveRequestRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import com.burntoburn.easyshift.service.leave.LeaveRequestFactory;
import com.burntoburn.easyshift.service.leave.LeaveRequestWorkerService;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaveRequestWorkerServiceImp implements LeaveRequestWorkerService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final LeaveRequestFactory leaveRequestFactory;

    @Transactional
    @Override
    public void createLeaveRequest(Long scheduleId, Long userId, LeaveRequestDto requestDto) {
        // user 확인
        User user = userRepository.findById(userId)
                .orElseThrow(UserException::userNotFound);

        // Schedule 확인
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleException::scheduleNotFound);

        for(LocalDate date: requestDto.getDates()) {
            boolean exists = leaveRequestRepository.existsByUserIdAndScheduleIdAndDate(userId, scheduleId, date);
            if(exists){
                throw LeaveException.leaveDuplicated();
            }

            LeaveRequest leaveRequest = leaveRequestFactory.createLeaveRequest(user, schedule, date);
            leaveRequestRepository.save(leaveRequest);
        }
    }

    @Override
    public LeaveRequest getLeaveRequest(Long leaveRequestId) {
        return leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(LeaveException::leaveNotFound);
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByUser(Long userId) {
        return leaveRequestRepository.findAllByUserId(userId);
    }

    @Transactional
    @Override
    public LeaveRequest updateLeaveRequest(Long leaveRequestId, LocalDate date) {
        LeaveRequest leaveRequest = getLeaveRequest(leaveRequestId);

        // 팩토리 패턴으로 수정
        leaveRequestFactory.updateLeaveRequest(leaveRequest, date);

        return leaveRequestRepository.save(leaveRequest); // [명시적 저장] JPA 의 변경 감지로 수정 예정
    }

    @Override
    public void cancelLeaveRequest(Long leaveRequestId, Long userId) {
        getLeaveRequest(leaveRequestId);
        leaveRequestRepository.deleteById(leaveRequestId);
    }
}
