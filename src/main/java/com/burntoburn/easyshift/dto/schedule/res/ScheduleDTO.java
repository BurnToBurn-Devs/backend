package com.burntoburn.easyshift.dto.schedule.res;

import com.burntoburn.easyshift.entity.schedule.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDTO {
    private Long schedulesId;
    private String scheduleName;
    private String scheduleMonth;
    private String status;
    private String description;

    public static ScheduleDTO fromEntity(Schedule schedule){
        return new ScheduleDTO(schedule.getId(),
                schedule.getScheduleName(),
                schedule.getScheduleMonth().toString(),
                schedule.getScheduleStatus().toString(),
                schedule.getDescription());
    }
}
