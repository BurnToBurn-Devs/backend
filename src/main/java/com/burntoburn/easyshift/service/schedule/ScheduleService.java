package com.burntoburn.easyshift.service.schedule;

import com.burntoburn.easyshift.dto.schedule.req.ScheduleUpload;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleDetailDTO;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleInfoResponse;

import com.burntoburn.easyshift.dto.schedule.res.WorkerScheduleResponse;
import com.burntoburn.easyshift.dto.store.SelectedScheduleTemplateDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ScheduleService {

    // 스케줄 생성
    void createSchedule(ScheduleUpload upload);

    // 스케줄 삭제
    void deleteSchedule(Long scheduleId);

    // 매장 스케줄 목록 조회
    ScheduleInfoResponse getSchedulesByStore(Long storeId, Pageable pageable);

    // worker의 스케줄 조회
    WorkerScheduleResponse getSchedulesByWorker(Long storeId, Long userId, String date);

    // 스케줄 조회 (일주일치)
    SelectedScheduleTemplateDto getWeeklySchedule(Long scheduleTemplateId, String date);

    // 스케줄 조회 (all)
    ScheduleDetailDTO getAllSchedules(Long scheduleId);


    // 스케줄 자동 배정
    void autoAssignSchedule(Long scheduleId);
}
