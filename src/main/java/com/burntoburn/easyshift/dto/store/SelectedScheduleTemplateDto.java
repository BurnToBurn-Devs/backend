package com.burntoburn.easyshift.dto.store;

import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SelectedScheduleTemplateDto {
    private Long scheduleTemplateId;
    private String scheduleTemplateName;
    private List<ShiftTemplateDto> shifts;

    public static SelectedScheduleTemplateDto fromEntity(ScheduleTemplate template, List<Shift> shifts) {
        List<ShiftTemplateDto> shiftTemplateDtos = template.getShiftTemplates().stream()
                .map(shiftTemplate -> ShiftTemplateDto.fromEntity(
                        shiftTemplate,
                        shifts.stream()
                                .filter(shift -> shift.getShiftTemplateId().equals(shiftTemplate.getId()))
                                .toList()
                ))
                .toList();

        return new SelectedScheduleTemplateDto(template.getId(), template.getScheduleTemplateName(), shiftTemplateDtos);
    }
}
