package com.burntoburn.easyshift.scheduler;

import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.user.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AutoAssignmentScheduler {

    /**
     * Shift 목록에 대해 자동 배정을 수행
     *
     * @param assignmentData 자동 배정에 필요한 데이터 (정렬된 Shift, 사용자 목록, 휴무 정보)
     */
    public void assignShifts(ShiftAssignmentData assignmentData) {
        List<Shift> shifts = assignmentData.shifts();
        List<User> users = assignmentData.users();
        Map<User, Set<LocalDate>> userLeaveDates = assignmentData.userLeaveDates();

        // 후보 큐 초기화 (회전 순서를 유지)
        ArrayDeque<User> candidateQueue = new ArrayDeque<>(users);
        User lastAssignedUser = null; // 마지막으로 배정된 유저 기록

        for (Shift shift : shifts) {
            // 큐가 비어있다면 초기 상태로 복구
            if (candidateQueue.isEmpty()) {
                candidateQueue.addAll(users);
            }
            //  가능한 유저 찾기
            User selectedUser = findNextAvailableUser(shift, candidateQueue, userLeaveDates, lastAssignedUser, users.size());

            //  유저가 있으면 배정하고 lastAssignedUser 갱신, 없으면 강제 배정
            if (selectedUser != null) {
                shift.assignUser(selectedUser);
                lastAssignedUser = selectedUser;
            } else {
                lastAssignedUser = forceAssignUser(shift, candidateQueue);
            }
        }
    }

    /**
     * 휴무가 아닌 첫 번째 가능한 유저를 찾는 메서드
     */
    private User findNextAvailableUser(Shift shift, ArrayDeque<User> candidateQueue,
                                       Map<User, Set<LocalDate>> userLeaveDates, User lastAssignedUser, int totalUsers) {
        int attempts = candidateQueue.size();
        while (attempts-- > 0) {
            User candidate = candidateQueue.poll();
            // 사이클이 새로 시작되었을 때, 첫 pop된 유저가 lastAssignedUser와 같다면 뒤로 넘김 -> 연속 배정 방지
            if (isFirstCandidateSameAsLastAssigned(candidate, lastAssignedUser, candidateQueue.size(), totalUsers)) {
                candidateQueue.offer(candidate);
                continue;
            }
            if (!isOnLeave(candidate, shift.getShiftDate(), userLeaveDates)) {
                return candidate;
            }
            candidateQueue.offer(candidate);
        }
        return null;
    }

    /**
     * 강제 배정이 필요한 경우, 큐의 맨 앞 유저를 배정하는 메서드
     */
    private User forceAssignUser(Shift shift, ArrayDeque<User> candidateQueue) {
        User forcedCandidate = candidateQueue.poll();
        shift.assignUser(forcedCandidate);
        return forcedCandidate;
    }

    /**
     * 사이클이 새로 시작될 때 첫 pop된 유저가 lastAssignedUser와 같다면 뒤로 넘겨야 하는지 판단
     */
    private boolean isFirstCandidateSameAsLastAssigned(User candidate, User lastAssignedUser, int currentQueueSize, int totalUsers) {
        return lastAssignedUser != null && candidate.equals(lastAssignedUser) && currentQueueSize == totalUsers - 1;
    }

    /**
     * 주어진 candidate가 shiftDate에 휴무인지 확인하는 메서드
     */
    private boolean isOnLeave(User candidate, LocalDate shiftDate, Map<User, Set<LocalDate>> userLeaveDates) {
        return userLeaveDates.containsKey(candidate) && userLeaveDates.get(candidate).contains(shiftDate);
    }
}
