package com.burntoburn.easyshift.controller.template;

import com.burntoburn.easyshift.common.response.ApiResponse;
import com.burntoburn.easyshift.dto.template.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.dto.template.ScheduleTemplateResponse;
import com.burntoburn.easyshift.dto.template.ScheduleTemplateWithShiftsResponse;
import com.burntoburn.easyshift.service.templates.ScheduleTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleTemplateController {
    private final ScheduleTemplateService scheduleTemplateService;

    /**
     * 스케줄 템플릿 생성 API
     * @param storeId 매장 ID (PathVariable)
     * @param request 생성할 스케줄 템플릿 정보 (RequestBody)
     * @return 생성된 스케줄 템플릿 정보를 포함한 DTO 응답
     */
    @PostMapping("/stores/{storeId}/schedule-templates")
    public ResponseEntity<ApiResponse<ScheduleTemplateResponse>> createScheduleTemplate(
            @PathVariable Long storeId,
            @RequestBody ScheduleTemplateRequest request) {
        ScheduleTemplateResponse response = scheduleTemplateService.createScheduleTemplate(storeId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * @param @param storeId 매장 ID
     * @return 해당 매장의 스케줄 템플릿 리스트 (DTO 변환 후 반환)
     **/
    @GetMapping("/stores/{storeId}/schedule-templates")
    public ResponseEntity<ApiResponse<ScheduleTemplateWithShiftsResponse>> getScheduleTemplate(@PathVariable Long storeId) {
        ScheduleTemplateWithShiftsResponse response = scheduleTemplateService
                .getAllScheduleTemplatesByStore(storeId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * @param scheduleTemplateId 삭제할 스케줄 템플릿 ID (PathVariable)*
     * @return 삭제 후 204 No Content 응답 반환
    **/
    @DeleteMapping("/schedule-templates/{scheduleTemplateId}")
    public ResponseEntity<ApiResponse<Void>> deleteScheduleTemplate(@PathVariable Long scheduleTemplateId){
        scheduleTemplateService.deleteScheduleTemplate(scheduleTemplateId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
