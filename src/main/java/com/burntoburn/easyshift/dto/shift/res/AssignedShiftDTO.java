package com.burntoburn.easyshift.dto.shift.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AssignedShiftDTO {
    private Long assignedShiftId;
    private Long userId;
    private String userName;
}
