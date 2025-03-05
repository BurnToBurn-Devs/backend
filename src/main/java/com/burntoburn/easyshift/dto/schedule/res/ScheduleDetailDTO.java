package com.burntoburn.easyshift.dto.schedule.res;


import com.burntoburn.easyshift.dto.store.ScheduleTemplateDto;
import com.burntoburn.easyshift.dto.store.ShiftTemplateDto;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDetailDTO {
    private Long scheduleId;
    private String scheduleName;
    private List<ShiftTemplateDto> shifts;

    public static ScheduleDetailDTO fromEntity(Long scheduleId, String scheduleName, List<ShiftTemplate> templates, List<Shift> shifts) {
        List<ShiftTemplateDto> templateDtos = templates.stream()
                .map(template -> ShiftTemplateDto.fromEntity(
                        template,
                        shifts.stream()
                                .filter(shift -> shift.getShiftTemplateId().equals(template.getId())) // ✅ 각 ShiftTemplate에 맞는 Shift 필터링
                                .toList()
                ))
                .toList();

        return new ScheduleDetailDTO(scheduleId, scheduleName, templateDtos);
    }

}
