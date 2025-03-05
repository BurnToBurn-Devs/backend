package com.burntoburn.easyshift.service.schedule;

import com.burntoburn.easyshift.dto.schedule.req.ScheduleUpload;
import com.burntoburn.easyshift.dto.schedule.req.ShiftDetail;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.ScheduleStatus;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScheduleFactory {

    public Schedule createSchedule(Store store, ScheduleTemplate scheduleTemplate, ScheduleUpload request) {
        Schedule schedule = Schedule.builder()
                .scheduleName(request.getScheduleName())
                .scheduleMonth(request.getScheduleMonth())
                .scheduleStatus(ScheduleStatus.PENDING)
                .scheduleTemplateId(scheduleTemplate.getId()) // Schedule 테이블에 scheduleTemplateId 저장
                .description(request.getDescription())
                .store(store)
                .shifts(new ArrayList<>())
                .build();

        List<ShiftDetail> shiftDetails = request.getShiftDetails();
        List<Shift> shifts = createShifts(schedule, scheduleTemplate, shiftDetails);
        schedule.getShifts().addAll(shifts);
        return schedule;
    }

    public List<Shift> createShifts(Schedule schedule, ScheduleTemplate scheduleTemplate, List<ShiftDetail> shiftDetails) {
        YearMonth scheduleMonth = schedule.getScheduleMonth();
        int daysInMonth = scheduleMonth.lengthOfMonth();
        Map<Long, Integer> shiftWorkerMap = new HashMap<>();

        for (ShiftDetail shiftRequest : shiftDetails) {
            shiftWorkerMap.put(shiftRequest.getShiftTemplateId(), shiftRequest.getExpectedWorkers());
        }

        List<Shift> shifts = new ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            shifts.addAll(createShiftsForDay(schedule, scheduleTemplate, day, shiftWorkerMap));
        }
        return shifts;
    }

    private List<Shift> createShiftsForDay(Schedule schedule, ScheduleTemplate scheduleTemplate, int day, Map<Long, Integer> shiftWorkerMap) {
        List<Shift> shifts = new ArrayList<>();
        Map<Long, ShiftTemplate> uniqueTemplates = new HashMap<>();

        for (ShiftTemplate shiftTemplate : scheduleTemplate.getShiftTemplates()) {
            uniqueTemplates.putIfAbsent(shiftTemplate.getId(), shiftTemplate);
        }

        for (ShiftTemplate shiftTemplate : uniqueTemplates.values()) {
            int expectedWorkers = shiftWorkerMap.getOrDefault(shiftTemplate.getId(), 1);
            for (int i = 0; i < expectedWorkers; i++) {
                shifts.add(createShift(schedule, shiftTemplate, day));
            }
        }
        return shifts;
    }

    private Shift createShift(Schedule schedule, ShiftTemplate shiftTemplate, int day) {
        return Shift.builder()
                .schedule(schedule)
                .shiftDate(schedule.getScheduleMonth().atDay(day))
                .shiftName(shiftTemplate.getShiftTemplateName())
                .shiftTemplateId(shiftTemplate.getId())
                .startTime(shiftTemplate.getStartTime())
                .endTime(shiftTemplate.getEndTime())
                .user(null)
                .build();
    }
}
