package com.burntoburn.easyshift.dto.schedule.res;

import com.burntoburn.easyshift.entity.schedule.Shift;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkerShift {
    private Long shiftId;
    private LocalDate shiftDate;
    private String shiftName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    public static WorkerShift fromEntity(Shift shift) {
        return new WorkerShift(
                shift.getId(),
                shift.getShiftDate(),
                shift.getShiftName(),
                shift.getStartTime(),
                shift.getEndTime()
        );
    }
}
