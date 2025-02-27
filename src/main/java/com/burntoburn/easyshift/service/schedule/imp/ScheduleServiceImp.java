package com.burntoburn.easyshift.service.schedule.imp;

import com.burntoburn.easyshift.dto.schedule.req.scheduleCreate.ScheduleRequest;
import com.burntoburn.easyshift.entity.leave.ApprovalStatus;
import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.exception.schedule.ScheduleException;
import com.burntoburn.easyshift.repository.leave.LeaveRequestRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.scheduler.AutoAssignmentScheduler;
import com.burntoburn.easyshift.scheduler.ShiftAssignmentData;
import com.burntoburn.easyshift.scheduler.ShiftAssignmentProcessor;
import com.burntoburn.easyshift.service.schedule.ScheduleFactory;
import com.burntoburn.easyshift.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleServiceImp implements ScheduleService {
    private final ScheduleFactory scheduleFactory;
    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final ScheduleRepository scheduleRepository;
    private final StoreRepository storeRepository;
    private final ShiftRepository shiftRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final ShiftAssignmentProcessor shiftAssignmentProcessor;
    private final AutoAssignmentScheduler autoAssignmentScheduler;

    /**
     * 스케줄 생성
     */
    @Transactional
    @Override
    public Schedule createSchedule(Long storeId, ScheduleRequest request) {
        // Store 확인
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NoSuchElementException("Store not found"));

        // scheduleTemplate 확인
        ScheduleTemplate scheduleTemplate = scheduleTemplateRepository.findById(request.getScheduleTemplateId())
                .orElseThrow(() -> new NoSuchElementException("ScheduleTemplate not found"));

        // 스케줄 생성 (ScheduleFactory 활용)
        Schedule schedule = scheduleFactory.createSchedule(store, scheduleTemplate, request);

        // 스케줄 저장 및 반환
        scheduleRepository.save(schedule);
        shiftRepository.saveAll(schedule.getShifts().getList());
        return schedule;
    }

    /**
     * 스케줄 삭제
     */
    @Transactional
    @Override
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
        scheduleRepository.delete(schedule);
    }

    /**
     * 스케줄 수정
     */
    @Transactional
    @Override
    public Schedule updateSchedule(Long scheduleId, ScheduleRequest request) {
        // 기존 스케줄 조회
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));

        // ScheduleFactory를 사용하여 업데이트 적용
        Schedule updatedSchedule = scheduleFactory.updateSchedule(existingSchedule, request);

        // 변경 감지를 통해 자동 반영
        return scheduleRepository.save(updatedSchedule);
    }

    /**
     * 매장의 모든 스케줄 조회
     */
    @Override
    public List<Schedule> getSchedulesByStore(Long storeId) {
        return scheduleRepository.findByStoreId(storeId);
    }

    @Override
    @Transactional
    public void autoAssignSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleException::scheduleNotFound);

        List<Shift> shifts = shiftRepository.findAllBySchedule(schedule);
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllByScheduleAndApprovalStatus(schedule, ApprovalStatus.APPROVED);

        // 데이터 전처리: 정렬된 Shift, 사용자 목록, 최대 필요 인원수 계산
        ShiftAssignmentData assignmentData = shiftAssignmentProcessor.processData(shifts, leaveRequests);

        // 현재 총 근무자 수가 최대 필요 인원수보다 적다면 예외 발생
        if (assignmentData.users().size() < assignmentData.maxRequired()) {
            throw ScheduleException.insufficientUsersForAssignment();
        }

        // 스케줄 자동 배정
        autoAssignmentScheduler.assignShifts(assignmentData);
    }

}
