package com.burntoburn.easyshift.dto.template.res;

import com.burntoburn.easyshift.dto.template.ScheduleTemplateResponse;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllScheduleTemplateResponse {
    private List<ScheduleTemplateResponse> scheduleTemplateResponses;


}
