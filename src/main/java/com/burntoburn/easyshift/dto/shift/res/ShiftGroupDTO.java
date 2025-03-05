package com.burntoburn.easyshift.dto.shift.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ShiftGroupDTO {
    private Long shiftId;
    private String shiftName;
    private String startTime;
    private String endTime;
    private List<ShiftDateDTO> dates;
}
