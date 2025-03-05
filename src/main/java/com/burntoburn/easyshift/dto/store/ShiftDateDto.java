package com.burntoburn.easyshift.dto.store;

import com.burntoburn.easyshift.entity.schedule.Shift;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftDateDto {
    private String date;
    private List<AssignedShiftDto> assignedShifts;

    public static ShiftDateDto fromEntity(LocalDate date, List<Shift> shifts) {
        List<AssignedShiftDto> assignedShiftDtos = shifts.stream()
                .map(AssignedShiftDto::fromEntity)
                .toList();

        return new ShiftDateDto(date.toString(), assignedShiftDtos);
    }
}
