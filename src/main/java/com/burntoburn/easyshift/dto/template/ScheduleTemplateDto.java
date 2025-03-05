package com.burntoburn.easyshift.dto.template;

import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleTemplateDto {
    private Long scheduleTemplateId;
    private String scheduleTemplateName;
    private List<ShiftTemplateDto> shiftTemplates;

    public static ScheduleTemplateDto fromEntity(ScheduleTemplate template) {
        List<ShiftTemplateDto> shiftTemplateDtos = template.getShiftTemplates().stream()
                .map(ShiftTemplateDto::fromEntity)
                .toList();

        return new ScheduleTemplateDto(template.getId(), template.getScheduleTemplateName(), shiftTemplateDtos);
    }
}
