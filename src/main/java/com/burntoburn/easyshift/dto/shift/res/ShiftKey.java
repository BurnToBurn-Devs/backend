package com.burntoburn.easyshift.dto.shift.res;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class ShiftKey {
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;
}
