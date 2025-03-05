package com.burntoburn.easyshift.dto.schedule.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ScheduleSummaryDTO {
    private Long scheduleId;
    private String scheduleName;
}
