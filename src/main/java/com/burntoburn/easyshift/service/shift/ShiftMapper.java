package com.burntoburn.easyshift.service.shift;

import com.burntoburn.easyshift.dto.shift.res.ShiftInfoDTO;
import com.burntoburn.easyshift.dto.shift.res.detailShift.ShiftDetailDto;
import com.burntoburn.easyshift.dto.shift.res.detailShift.UserInfoDTO;
import com.burntoburn.easyshift.entity.schedule.Shift;
import org.springframework.stereotype.Component;

@Component
public class ShiftMapper {
    public ShiftInfoDTO toDto(Shift shift) {
        if (shift == null) {
            return null;
        }
        return ShiftInfoDTO.builder()
                .id(shift.getId())
                .scheduleId(shift.getSchedule() != null ? shift.getSchedule().getId() : null)
                .shiftName(shift.getShiftName())
                .shiftDate(shift.getShiftDate())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .userId(shift.getUser() != null ? shift.getUser().getId() : null)
                .build();
    }

    public ShiftDetailDto toDetailDto(Shift shift) {
        if (shift == null) {
            return null;
        }

        UserInfoDTO userDto = null;
        if (shift.getUser() != null) {
            userDto = UserInfoDTO.builder()
                    .id(shift.getUser().getId())
                    .username(shift.getUser().getName())
                    .email(shift.getUser().getEmail())
                    .build();
        }

        return ShiftDetailDto.builder()
                .id(shift.getId())
                .scheduleId(shift.getSchedule() != null ? shift.getSchedule().getId() : null)
                .shiftName(shift.getShiftName())
                .shiftDate(shift.getShiftDate())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .user(userDto)
                .build();
    }

}
