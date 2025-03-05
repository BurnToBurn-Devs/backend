package com.burntoburn.easyshift.dto.store;

import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftTemplateDto {
    private Long shiftTemplateId;
    private String shiftTemplateName;
    private String startTime;
    private String endTime;
    private List<ShiftDateDto> dates;

    public static ShiftTemplateDto fromEntity(ShiftTemplate shiftTemplate, List<Shift> shifts) {
        List<ShiftDateDto> shiftDateDtos = shifts.stream()
                .collect(Collectors.groupingBy(Shift::getShiftDate))
                .entrySet().stream()
                .map(entry -> ShiftDateDto.fromEntity(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new ShiftTemplateDto(
                shiftTemplate.getId(),
                shiftTemplate.getShiftTemplateName(),
                shiftTemplate.getStartTime().toString(),
                shiftTemplate.getEndTime().toString(),
                shiftDateDtos
        );
    }
}
