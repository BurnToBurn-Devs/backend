package com.burntoburn.easyshift.service.schedule.imp;

import com.burntoburn.easyshift.dto.schedule.req.ScheduleUpload;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleDetailDTO;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleInfoResponse;
import com.burntoburn.easyshift.dto.schedule.res.WorkerScheduleResponse;
import com.burntoburn.easyshift.dto.store.SelectedScheduleTemplateDto;
import com.burntoburn.easyshift.entity.leave.ApprovalStatus;
import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.exception.schedule.ScheduleException;
import com.burntoburn.easyshift.exception.shift.ShiftException;
import com.burntoburn.easyshift.exception.template.TemplateException;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 스케줄 생성
    @Transactional
    @Override
    public void createSchedule(ScheduleUpload upload) {
        Store store = storeRepository.findById(upload.getStoreId())
                .orElseThrow(ScheduleException::scheduleNotFound);

        ScheduleTemplate scheduleTemplate = scheduleTemplateRepository.findById(upload.getScheduleTemplateId())
                .orElseThrow(TemplateException::scheduleTemplateNotFound);

        Schedule schedule = scheduleFactory.createSchedule(store, scheduleTemplate, upload);
        scheduleRepository.save(schedule);
    }

    // 스케줄 삭제
    @Transactional
    @Override
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleException::scheduleNotFound);
        scheduleRepository.delete(schedule);
    }

    // 매장 스케줄 목록 조회
    @Override
    public ScheduleInfoResponse getSchedulesByStore(Long storeId, Pageable pageable) {
        Page<Schedule> schedulePage = Optional.of(scheduleRepository.findByStoreIdOrderByCreatedAtDesc(storeId, pageable))
                .filter(page -> !page.isEmpty())
                .orElseThrow(ScheduleException::scheduleNotFound);

        return ScheduleInfoResponse.formEntity(schedulePage, schedulePage.isLast());
    }

    // worker의 스케줄 조회
    @Override
    public WorkerScheduleResponse getSchedulesByWorker(Long storeId, Long userId, String date) {
        YearMonth scheduleMonth = YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy-MM"));

        List<Schedule> workerSchedules = scheduleRepository.findWorkerSchedules(storeId, scheduleMonth, userId);
        if (workerSchedules.isEmpty()) {
            throw ScheduleException.scheduleNotFound();
        }

        Store store = workerSchedules.stream().findFirst()
                .map(Schedule::getStore)
                .orElseThrow(ScheduleException::scheduleNotFound);

        return WorkerScheduleResponse.fromEntity(store, workerSchedules);
    }

    // 스케줄 조회(일주일치)
    @Override
    public SelectedScheduleTemplateDto getWeeklySchedule(Long scheduleTemplateId, String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate monday = localDate.with(DayOfWeek.MONDAY);
        LocalDate endDate = monday.plusDays(6);

        List<Schedule> schedules = scheduleRepository.findSchedulesWithTemplate(scheduleTemplateId);
        if (schedules.isEmpty()) {
            throw ScheduleException.scheduleNotFound();
        }

        List<Long> scheduleIds = schedules.stream().map(Schedule::getId).toList();
        if (scheduleIds.isEmpty()) {
            throw ScheduleException.scheduleNotFound();
        }
        List<Shift> shifts = shiftRepository.findShiftsByScheduleIdWithUser(scheduleIds, monday, endDate);
        if (shifts.isEmpty()) {
            throw ShiftException.shiftNotFound();
        }


        ScheduleTemplate scheduleTemplate = scheduleTemplateRepository
                .findScheduleTemplateWithShiftsById(schedules.get(0).getScheduleTemplateId())
                .orElseThrow(TemplateException::scheduleTemplateNotFound);

        return SelectedScheduleTemplateDto.fromEntity(scheduleTemplate, shifts);
    }

    // 스케줄 조회(all)
    @Transactional
    @Override
    public ScheduleDetailDTO getAllSchedules(Long scheduleId) {
        Schedule schedule = scheduleRepository.findScheduleWithShifts(scheduleId)
                .orElseThrow(ScheduleException::scheduleNotFound);

        ScheduleTemplate scheduleTemplate = scheduleTemplateRepository
                .findScheduleTemplateWithShiftsById(schedule.getScheduleTemplateId())
                .orElseThrow(TemplateException::scheduleTemplateNotFound);

        List<ShiftTemplate> shiftTemplates = scheduleTemplate.getShiftTemplates();
        if (shiftTemplates == null) {
            throw new IllegalStateException("Shift templates list is unexpectedly null");
        }
        if (shiftTemplates.isEmpty()) {
            throw TemplateException.shiftTemplateNotFound();
        }

        List<Shift> shifts = schedule.getShifts();
        if (shifts == null || shifts.isEmpty()) {
            throw ShiftException.shiftNotFound();
        }

        return ScheduleDetailDTO.fromEntity(scheduleId, schedule.getScheduleName(), shiftTemplates, shifts);
    }

    @Override
    @Transactional
    public void autoAssignSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(ScheduleException::scheduleNotFound);

        List<Shift> shifts = shiftRepository.findAllBySchedule(schedule);
        if (shifts == null || shifts.isEmpty()) {
            throw ShiftException.shiftNotFound();
        }

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAllByScheduleAndApprovalStatus(schedule, ApprovalStatus.APPROVED);

        ShiftAssignmentData assignmentData = shiftAssignmentProcessor.processData(shifts, leaveRequests);
        if (assignmentData.users().size() < assignmentData.maxRequired()) {
            throw ScheduleException.insufficientUsersForAssignment();
        }

        autoAssignmentScheduler.assignShifts(assignmentData);
    }
}
