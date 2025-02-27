package com.burntoburn.easyshift.scheduler;

import com.burntoburn.easyshift.entity.leave.ApprovalStatus;
import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ShiftAssignmentProcessorTest {

    @InjectMocks
    private ShiftAssignmentProcessor shiftAssignmentProcessor;

    private List<Shift> shifts;
    private List<LeaveRequest> leaveRequests;

    private final User userA = new User(1L, "userA", null, null, null, null);
    private final User userB = new User(2L, "userB", null, null, null, null);
    private final User userC = new User(3L, "userC", null, null, null, null);

    @BeforeEach
    void setUp() {
        // 정렬되지 않은 Shift 데이터 생성 (정렬 테스트 목적)
        shifts = new ArrayList<>(List.of(
                new Shift(4L, "Afternoon Shift", LocalDate.of(2024, 11, 3), LocalTime.of(13, 0), LocalTime.of(16, 0), null, null),
                new Shift(2L, "Morning Shift", LocalDate.of(2024, 11, 1), LocalTime.of(9, 0), LocalTime.of(12, 0), null, null),
                new Shift(5L, "Evening Shift", LocalDate.of(2024, 11, 2), LocalTime.of(17, 0), LocalTime.of(20, 0), null, null),
                new Shift(1L, "Morning Shift", LocalDate.of(2024, 11, 1), LocalTime.of(9, 0), LocalTime.of(12, 0), null, null),
                new Shift(3L, "Afternoon Shift", LocalDate.of(2024, 11, 2), LocalTime.of(13, 0), LocalTime.of(16, 0), null, null)
        ));

        // APPROVED된 LeaveRequest만 생성
        leaveRequests = List.of(
                new LeaveRequest(1L, LocalDate.of(2024, 11, 1), ApprovalStatus.APPROVED, userA, null),
                new LeaveRequest(2L, LocalDate.of(2024, 11, 2), ApprovalStatus.APPROVED, userB, null),
                new LeaveRequest(3L, LocalDate.of(2024, 11, 3), ApprovalStatus.APPROVED, userC, null)
        );
    }

    @Test
    @DisplayName("Shift 데이터를 날짜 및 시작 시간 기준으로 정렬해야 한다.")
    void shouldSortShiftsByDateAndStartTime() {
        ShiftAssignmentData result = shiftAssignmentProcessor.processData(shifts, leaveRequests);

        // Shift가 날짜 → 시간 순으로 정렬되었는지 확인
        assertThat(result.shifts()).isSortedAccordingTo((s1, s2) ->
                s1.getShiftDate().compareTo(s2.getShiftDate()) != 0 ?
                        s1.getShiftDate().compareTo(s2.getShiftDate()) :
                        s1.getStartTime().compareTo(s2.getStartTime()));
    }

    @Test
    @DisplayName("사용자별 휴무일 정보를 올바르게 매핑해야 한다.")
    void shouldMapApprovedLeaveRequestsToUsersCorrectly() {
        ShiftAssignmentData result = shiftAssignmentProcessor.processData(shifts, leaveRequests);

        // APPROVED된 사용자만 휴무로 반영됨
        assertThat(result.userLeaveDates()).containsOnlyKeys(userA, userB, userC);
        assertThat(result.userLeaveDates().get(userA)).containsExactly(LocalDate.of(2024, 11, 1));
        assertThat(result.userLeaveDates().get(userB)).containsExactly(LocalDate.of(2024, 11, 2));
        assertThat(result.userLeaveDates().get(userC)).containsExactly(LocalDate.of(2024, 11, 3));
    }

    @Test
    @DisplayName("최대 필요 근무 인원을 정확하게 계산해야 한다.")
    void shouldCalculateMaxRequiredShiftsCorrectly() {
        ShiftAssignmentData result = shiftAssignmentProcessor.processData(shifts, leaveRequests);

        // 최대 필요 인원 계산 (9~12시: 2명, 13~16시: 2명, 17~20시: 1명 → 최댓값: 2)
        assertThat(result.maxRequired()).isEqualTo(2);
    }

    @Test
    @DisplayName("휴무 요청이 없는 경우 올바르게 처리해야 한다.")
    void shouldHandleNoLeaveRequestsGracefully() {
        ShiftAssignmentData result = shiftAssignmentProcessor.processData(shifts, List.of());

        // ✅ 휴무 요청이 없으면 users 리스트가 비어 있어야 함
        assertThat(result.users()).isEmpty();
        assertThat(result.userLeaveDates()).isEmpty();
    }
}
