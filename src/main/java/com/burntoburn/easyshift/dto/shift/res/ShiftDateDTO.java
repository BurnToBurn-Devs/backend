package com.burntoburn.easyshift.dto.shift.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ShiftDateDTO {
    private String date;
    private List<AssignedShiftDTO> assignedShifts;
}
