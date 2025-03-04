package com.burntoburn.easyshift.controller.schedule;

import com.burntoburn.easyshift.common.response.ApiResponse;
import com.burntoburn.easyshift.dto.schedule.req.ScheduleUpload;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleDetailDTO;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleInfoResponse;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleResponse;
import com.burntoburn.easyshift.dto.schedule.res.WorkerScheduleResponse;
import com.burntoburn.easyshift.dto.store.SelectedScheduleTemplateDto;
import com.burntoburn.easyshift.service.schedule.ScheduleService;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleController {
    private final ScheduleService scheduleService;

    /**
     * 스케줄 생성 API
     * @param upload 스케줄 생성 요청 DTO
     */
    @PostMapping("/schedules")
    public ResponseEntity<ApiResponse<Void>> createSchedule(@RequestBody ScheduleUpload upload) {
        scheduleService.createSchedule(upload);

        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 특정 Worker의 특정 날짜 스케줄 조회 API
     * @param storeId 매장 ID
     * @param userId 워커 ID
     * @param date 조회할 날짜
     * @return 해당 날짜의 Worker 스케줄 목록
     */
    @GetMapping("/stores/{storeId}/workers/{userId}/schedules")
    public ResponseEntity<ApiResponse<WorkerScheduleResponse>> getWorkerSchedule(@PathVariable Long storeId,
                                                                                 @PathVariable Long userId,
                                                                                 @RequestParam String date) {
        WorkerScheduleResponse response = scheduleService.getSchedulesByWorker(storeId, userId, date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 매장 스케줄 목록 조회
     * @param storeId 매장 ID
     * @return 해당 매장의 스케줄 목록
     */
    @GetMapping("/stores/{storeId}/schedules")
    public ResponseEntity<ApiResponse<ScheduleInfoResponse>> getStoreSchedules(@PathVariable Long storeId, Pageable pageable) {
        ScheduleInfoResponse response = scheduleService.getSchedulesByStore(storeId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 스케줄 조회(일주일치)
     * @param scheduleTemplateId 스케줄 ID
     * @param date 조회할 날짜 (쿼리 파라미터)
     * @return 해당 날짜의 스케줄 상세 정보
     */
    @GetMapping("/schedules/{scheduleTemplateId}")
    public ResponseEntity<ApiResponse<SelectedScheduleTemplateDto>> getSchedule(@PathVariable Long scheduleTemplateId,
                                                                  @RequestParam(required = false) String  date) {
        SelectedScheduleTemplateDto response = scheduleService.getWeeklySchedule(scheduleTemplateId, date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 스케줄 조회 all + Shift 포함 조회
     * @param scheduleId 스케줄 ID
     * @return 해당 날짜의 스케줄 상세 정보
     */
    @GetMapping("/schedules/{scheduleId}/all")
    public ResponseEntity<ApiResponse<ScheduleDetailDTO>> getScheduleAll(@PathVariable Long scheduleId) {
        ScheduleDetailDTO res = scheduleService.getAllSchedules(scheduleId);
        return ResponseEntity.ok(ApiResponse.success(res));
    }


    /**
     * 스케줄 삭제 API
     * @param scheduleId 삭제할 스케줄 ID
     * @return 204 No Content 응답
     */
    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(ApiResponse.success());
    }


}
