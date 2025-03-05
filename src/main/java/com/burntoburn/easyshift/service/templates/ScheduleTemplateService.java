package com.burntoburn.easyshift.service.templates;

import com.burntoburn.easyshift.dto.template.ScheduleTemplateResponse;
import com.burntoburn.easyshift.dto.template.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.dto.template.ScheduleTemplateWithShiftsResponse;

public interface ScheduleTemplateService {

    // 스케줄 템플릿 생성
    ScheduleTemplateResponse createScheduleTemplate(Long storeId, ScheduleTemplateRequest request);

    // ShiftTemplate 조회 - 전체
    ScheduleTemplateWithShiftsResponse getAllScheduleTemplatesByStore(Long storeId);

    // 스케줄 템플릿 삭제
    void deleteScheduleTemplate(Long scheduleTemplateId);
}
