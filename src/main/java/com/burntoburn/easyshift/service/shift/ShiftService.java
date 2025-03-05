package com.burntoburn.easyshift.service.shift;

import com.burntoburn.easyshift.dto.shift.req.ShiftUpload;
import com.burntoburn.easyshift.entity.schedule.Shift;
import java.util.List;

public interface ShiftService{
    // Create (새로운 Shift 생성)
    Shift createShift(Long scheduleId, ShiftUpload shiftUpload);

    // Read (단일 및 전체 조회)
    Shift getShiftOne(Long id);
    List<Shift> getAllShifts();
    Shift getShiftWithUser(Long ShiftId);

    // Update (Shift 수정)
    Shift updateShift(Long id, ShiftUpload shiftUpload);


    // Delete (Shift 삭제)
    void deleteShift(Long id);

    // 근무자 변경
    void updateUserShift(Long userId, Long shiftId);
}
