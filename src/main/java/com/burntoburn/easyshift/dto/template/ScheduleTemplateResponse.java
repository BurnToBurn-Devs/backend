package com.burntoburn.easyshift.dto.template;

import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleTemplateResponse {
    private Long ScheduleTemplateId;
    private String scheduleTemplateName;
    private Long storeId;
    private List<ShiftTemplateDto> shiftTemplateDtos;

    public static ScheduleTemplateResponse fromEntity(ScheduleTemplate template) {
        List<ShiftTemplateDto> shiftTemplateDtos = template.getShiftTemplates().stream()
                .map(ShiftTemplateDto::fromEntity)
                .toList();

        return new ScheduleTemplateResponse(
                template.getId(),
                template.getScheduleTemplateName(),
                template.getStore().getId(), // ✅ storeId 추가
                shiftTemplateDtos);
        }
    }
