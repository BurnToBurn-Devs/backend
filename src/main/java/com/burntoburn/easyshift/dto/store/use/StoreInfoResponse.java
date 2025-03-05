package com.burntoburn.easyshift.dto.store.use;

import com.burntoburn.easyshift.dto.store.ScheduleTemplateDto;
import com.burntoburn.easyshift.dto.store.SelectedScheduleTemplateDto;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreInfoResponse {
    private Long storeId;
    private List<ScheduleTemplateDto> scheduleTemplates;
    private SelectedScheduleTemplateDto selectedScheduleTemplate;

    public static StoreInfoResponse fromEntity(Long storeId, List<ScheduleTemplate> templates, ScheduleTemplate selectedTemplate, List<Shift> shifts) {
        List<ScheduleTemplateDto> templateDtos = templates.stream()
                .map(ScheduleTemplateDto::fromEntity)
                .toList();

        SelectedScheduleTemplateDto selectedTemplateDto = SelectedScheduleTemplateDto.fromEntity(selectedTemplate, shifts);
        return new StoreInfoResponse(storeId, templateDtos, selectedTemplateDto);
    }
}