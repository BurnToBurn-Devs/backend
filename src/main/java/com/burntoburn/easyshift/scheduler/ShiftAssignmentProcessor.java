package com.burntoburn.easyshift.scheduler;

import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.user.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ShiftAssignmentProcessor {

    /**
     * Shift 목록과 LeaveRequest를 기반으로 배정에 필요한 데이터 가공
     *
     * @param shifts        해당 스케줄에 포함된 Shift 목록
     * @param leaveRequests 승인된 휴무 신청 목록
     * @return 배정에 필요한 데이터 객체 (정렬된 Shift, 사용자 목록, 휴무 정보, 최대 필요 인원수)
     */
    public ShiftAssignmentData processData(List<Shift> shifts, List<LeaveRequest> leaveRequests) {
        // Shift 정렬 (날짜, 시작 시간 순)
        shifts.sort(Comparator.comparing(Shift::getShiftDate).thenComparing(Shift::getStartTime));

        // 사용자별 휴무일 집합 생성
        Map<User, Set<LocalDate>> userLeaveDates = new HashMap<>();
        for (LeaveRequest lr : leaveRequests) {
            userLeaveDates.computeIfAbsent(lr.getUser(), k -> new HashSet<>()).add(lr.getDate());
        }

        // 배정 대상 사용자 목록 생성 (휴무 신청이 승인된 사용자만 포함)
        List<User> users = userLeaveDates.keySet().stream()
                .sorted(Comparator.comparing(User::getId))
                .toList();

        // 파트타임별 최대 필요 인원수 계산
        long maxRequired = shifts.stream()
                .collect(Collectors.groupingBy(
                        shift -> shift.getShiftDate().toString() + "_" + shift.getStartTime() + "_" + shift.getEndTime(),
                        Collectors.counting()
                ))
                .values().stream().mapToLong(Long::longValue)
                .max().orElse(0);

        return new ShiftAssignmentData(shifts, users, userLeaveDates, maxRequired);
    }
}

