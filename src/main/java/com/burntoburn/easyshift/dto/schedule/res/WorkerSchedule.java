package com.burntoburn.easyshift.dto.schedule.res;

import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkerSchedule {
    private Long scheduleId;
    private String scheduleName;
    private String scheduleMonth;
    private List<WorkerShift> shifts;

    public static WorkerSchedule fromEntity(Schedule schedule) {
        List<WorkerShift> shiftDTOs = schedule.getShifts() == null ?
                List.of() :
                schedule.getShifts().stream()
                        .map(WorkerShift::fromEntity)
                        .toList();

        return new WorkerSchedule(
                schedule.getId(),
                schedule.getScheduleName(),
                schedule.getScheduleMonth().toString(),
                shiftDTOs
        );
    }
}
