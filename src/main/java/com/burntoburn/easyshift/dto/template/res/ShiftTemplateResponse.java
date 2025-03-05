package com.burntoburn.easyshift.dto.template.res;

import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShiftTemplateResponse {
    private Long shiftTemplateId;
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;

    public static ShiftTemplateResponse fromEntity(ShiftTemplate shiftTemplate) {
        return ShiftTemplateResponse.builder()
                .shiftTemplateId(shiftTemplate.getId())
                .shiftName(shiftTemplate.getShiftTemplateName())
                .startTime(shiftTemplate.getStartTime())
                .endTime(shiftTemplate.getEndTime())
                .build();
    }
}
