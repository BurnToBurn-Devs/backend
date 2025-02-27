package com.burntoburn.easyshift.scheduler;

import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record ShiftAssignmentData(
        List<Shift> shifts,
        List<User> users,
        Map<User, Set<LocalDate>> userLeaveDates,
        long maxRequired
) {
}
