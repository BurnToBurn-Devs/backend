package com.burntoburn.easyshift.dto.store;

import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleTemplateDto {
    private Long scheduleTemplateId;
    private String scheduleTemplateName;

    public static ScheduleTemplateDto fromEntity(ScheduleTemplate template) {
        return new ScheduleTemplateDto(template.getId(), template.getScheduleTemplateName());
    }
}
