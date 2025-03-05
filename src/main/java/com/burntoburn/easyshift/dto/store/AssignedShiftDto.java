package com.burntoburn.easyshift.dto.store;

import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AssignedShiftDto {
    private Long shiftId;
    private Long userId;
    private String userName;

    public static AssignedShiftDto fromEntity(Shift shift) {
        User user = shift.getUser(); // User가 null일 수 있음

        return new AssignedShiftDto(
                shift.getId(),
                (user != null) ? user.getId() : null,
                (user != null) ? user.getName() : null
        );
    }
}