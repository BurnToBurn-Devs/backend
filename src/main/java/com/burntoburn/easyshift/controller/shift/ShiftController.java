package com.burntoburn.easyshift.controller.shift;

import com.burntoburn.easyshift.common.response.ApiResponse;
import com.burntoburn.easyshift.dto.shift.ShiftUpdateUser;
import com.burntoburn.easyshift.dto.shift.req.ShiftUpload;
import com.burntoburn.easyshift.dto.shift.res.ShiftInfoDTO;
import com.burntoburn.easyshift.dto.shift.res.detailShift.ShiftDetailDto;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.service.shift.ShiftMapper;
import com.burntoburn.easyshift.service.shift.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Shift 관련 API 컨트롤러 예시
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShiftController {
    private final ShiftService shiftService;
    private final ShiftMapper shiftMapper;

    // 특정 Shift 조회
    // @GetMapping("/shifts/{shiftId}")
    public ResponseEntity<ShiftDetailDto> getShiftById(@PathVariable Long shiftId) {
        Shift shift = shiftService.getShiftOne(shiftId);
        ShiftDetailDto detailDto = shiftMapper.toDetailDto(shift);

        return ResponseEntity.ok(detailDto);
    }

    // Shift 생성
    // @PostMapping("/schedules/{scheduleId}/shifts")
    public ResponseEntity<ShiftInfoDTO> createShift(@PathVariable Long scheduleId,
                                                    @RequestBody ShiftUpload shiftUpload /* Shift 생성 DTO */) {

        Shift shift = shiftService.createShift(scheduleId, shiftUpload);
        return ResponseEntity.ok(shiftMapper.toDto(shift));
    }

    // Shift 수정
    //@PatchMapping("/shifts/{shiftId}")
    public ResponseEntity<ShiftInfoDTO> updateShift(@PathVariable Long shiftId,
                                                    @RequestBody ShiftUpload updateRequest /* Shift 수정 DTO */) {
        Shift shift = shiftService.updateShift(shiftId, updateRequest);

        return ResponseEntity.ok(shiftMapper.toDto(shift));    }

    // Shift 삭제
    /**
     * @param shiftId 삭제할 Shift ID (PathVariable)
     * @return 삭제 후 204 No Content 응답 반환
     */
    @DeleteMapping("/shifts/{shiftId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deleteShift(@PathVariable Long shiftId) {
        shiftService.deleteShift(shiftId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // Shift 수정
    @PatchMapping("/shifts/{shiftId}")
    public ResponseEntity<ApiResponse<Void>> updateUserShift(@PathVariable Long shiftId, @RequestBody ShiftUpdateUser request) {
        shiftService.updateUserShift(request.getUserId(), shiftId);

        return ResponseEntity.ok(ApiResponse.success());
    }

}
