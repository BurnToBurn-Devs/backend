package com.burntoburn.easyshift.dto.shift.res.detailShift;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShiftDetailDto {
    private Long id;             // Shift 고유 ID
    private Long scheduleId;     // 해당 Shift가 속한 Schedule의 ID
    private String shiftName;    // Shift 이름
    private LocalDate shiftDate; // 근무 날짜 (YYYY-MM-DD)
    private LocalTime startTime; // 시작 시간 (HH:mm)
    private LocalTime endTime;   // 종료 시간 (HH:mm)
    private UserInfoDTO user;
}
