package com.burntoburn.easyshift.dto.schedule.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftDetail {
    private Long shiftTemplateId;  // ShiftTemplate의 ID
    private int expectedWorkers;   // 해당 Shift에 배정할 인원 수
}
