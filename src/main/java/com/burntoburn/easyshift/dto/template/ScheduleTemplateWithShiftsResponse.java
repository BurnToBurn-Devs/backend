package com.burntoburn.easyshift.dto.template;

import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleTemplateWithShiftsResponse {
    private List<ScheduleTemplateDto> scheduleTemplates;
    public static ScheduleTemplateWithShiftsResponse fromEntities(List<ScheduleTemplate> templates) {
        List<ScheduleTemplateDto> scheduleTemplateDtos = templates.stream()
                .map(ScheduleTemplateDto::fromEntity)
                .toList();

        return new ScheduleTemplateWithShiftsResponse(scheduleTemplateDtos);
    }
}

