package com.burntoburn.easyshift.dto.schedule.res;

import com.burntoburn.easyshift.entity.schedule.Schedule;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleInfoResponse {
    private List<ScheduleDTO> schedules;
    private PageInfoDTO pageInfoDTO;

    public static ScheduleInfoResponse formEntity(Page<Schedule> schedules, boolean isLast){
        List<ScheduleDTO> list = schedules.stream()
                .map(ScheduleDTO::fromEntity)
                .toList();

        PageInfoDTO pageInf = PageInfoDTO.fromEntity(isLast);
        return new ScheduleInfoResponse(list, pageInf);
    }
}
