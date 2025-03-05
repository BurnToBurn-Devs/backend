package com.burntoburn.easyshift.dto.schedule.res.ScheduleWithShifts;

import java.time.YearMonth;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleWithShiftsDto {
    private Long scheduleId;
    private String scheduleName;
    private YearMonth scheduleMonth;
    private String scheduleStatus;
    private List<ShiftsDto> shifts;
}
