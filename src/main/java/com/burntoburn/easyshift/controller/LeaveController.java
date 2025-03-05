package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.common.response.ApiResponse;
import com.burntoburn.easyshift.dto.leave.req.LeaveRequestDto;
import com.burntoburn.easyshift.service.leave.LeaveRequestWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveRequestWorkerService leaveRequestWorkerService;

    @PostMapping("/{schedule_id}/leave-requests")
    public ResponseEntity<ApiResponse<Void>> createLeaveRequest(
            @PathVariable("schedule_id") Long schedule_id,
            @RequestParam Long userId,
            @RequestBody LeaveRequestDto dates
    ) {
        leaveRequestWorkerService.createLeaveRequest(schedule_id, userId, dates);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
