package com.burntoburn.easyshift.scheduler;

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
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AutoAssignmentSchedulerTest {

    @InjectMocks
    private AutoAssignmentScheduler autoAssignmentScheduler;

    private List<Shift> shifts;
    private List<User> users;
    private Map<User, Set<LocalDate>> userLeaveDates;

    private final User userA = new User(1L, "userA", null, null, null, null);
    private final User userB = new User(2L, "userB", null, null, null, null);
    private final User userC = new User(3L, "userC", null, null, null, null);
    private final User userD = new User(4L, "userD", null, null, null, null);

    @BeforeEach
    void setUp() {
        // 정렬된 Shift 데이터
        shifts = new ArrayList<>(List.of(
                new Shift(1L, "Morning Shift", LocalDate.of(2024, 11, 1), LocalTime.of(9, 0), LocalTime.of(12, 0), null, null),
                new Shift(2L, "Afternoon Shift", LocalDate.of(2024, 11, 1), LocalTime.of(13, 0), LocalTime.of(16, 0), null, null),
                new Shift(3L, "Evening Shift", LocalDate.of(2024, 11, 1), LocalTime.of(17, 0), LocalTime.of(20, 0), null, null)
        ));
        users = new ArrayList<>(List.of(userA, userB, userC));

        // 동적으로 휴무일을 설정할 수 있도록 초기화
        userLeaveDates = users.stream()
                .collect(Collectors.toMap(user -> user, user -> new HashSet<>()));
    }

    @Test
    @DisplayName("모든 Shift가 순환하는 방식으로 올바르게 배정되는지 테스트")
    void shouldAssignShiftsInRoundRobinOrder() {
        autoAssignmentScheduler.assignShifts(new ShiftAssignmentData(shifts, users, userLeaveDates, 1));

        // 모든 Shift가 사용자에게 할당되었는지 확인
        assertThat(shifts).allMatch(shift -> shift.getUser() != null);

        // 사용자가 순환하며 배정되었는지 확인
        assertThat(shifts.get(0).getUser()).isEqualTo(userA);
        assertThat(shifts.get(1).getUser()).isEqualTo(userB);
        assertThat(shifts.get(2).getUser()).isEqualTo(userC);
    }

    @Test
    @DisplayName("일부 사용자가 특정 날짜에 휴무일 경우, 적절히 건너뛰고 배정되는지 테스트")
    void shouldSkipUsersOnLeave() {
        // userA는 11월 1일 휴무 설정
        userLeaveDates.put(userA, Set.of(LocalDate.of(2024, 11, 1)));

        // userD 추가
        users.add(userD);
        userLeaveDates.put(userD, new HashSet<>());

        // 실행
        autoAssignmentScheduler.assignShifts(new ShiftAssignmentData(shifts, users, userLeaveDates, 1));

        // userA가 11월 1일에는 배정되지 않았는지 확인
        assertThat(shifts.stream()
                .filter(shift -> shift.getShiftDate().equals(LocalDate.of(2024, 11, 1)))
                .map(Shift::getUser))
                .doesNotContain(userA);

        // 순차적으로 배정되는지 확인
        assertThat(shifts.get(0).getUser()).isEqualTo(userB);
        assertThat(shifts.get(1).getUser()).isEqualTo(userC);
        assertThat(shifts.get(2).getUser()).isEqualTo(userD);
    }

    @Test
    @DisplayName("특정 사용자의 휴무일을 반영하되, 강제 배정이 수행되는지 테스트")
    void shouldRespectUserLeaveButForceAssignIfNecessary() {
        // userA만 11월 1일을 휴무로 설정
        userLeaveDates.put(userA, Set.of(LocalDate.of(2024, 11, 1)));

        // 실행
        autoAssignmentScheduler.assignShifts(new ShiftAssignmentData(shifts, users, userLeaveDates, 1));

        // 모든 Shift가 사용자에게 배정되었는지 확인
        assertThat(shifts).allMatch(shift -> shift.getUser() != null);

        // 가능한 경우 userA의 휴무일을 반영하여 다른 사용자에게 배정되었는지 확인
        assertThat(shifts.get(0).getUser()).isEqualTo(userB);
        assertThat(shifts.get(1).getUser()).isEqualTo(userC);
        assertThat(shifts.get(2).getUser()).isEqualTo(userA); // 한 사이클 내에서 강제 배정됨
    }


    @Test
    @DisplayName("특정 날짜에 필요한 인원 수보다 사용 가능 인원이 많은 경우, 공평하게 배정되는지 테스트")
    void shouldDistributeShiftsEvenlyWhenMoreUsersThanRequired() {
        // 추가 사용자 추가
        users.add(userD);

        // 실행
        autoAssignmentScheduler.assignShifts(new ShiftAssignmentData(shifts, users, userLeaveDates, 2));

        // 모든 Shift가 배정되었는지 확인
        assertThat(shifts).allMatch(shift -> shift.getUser() != null);
        assertThat(shifts.get(0).getUser()).isEqualTo(userA);
        assertThat(shifts.get(1).getUser()).isEqualTo(userB);
        assertThat(shifts.get(2).getUser()).isEqualTo(userC);
    }

    @Test
    @DisplayName("각 사용자가 하루씩 휴무를 가지는 경우, 휴무를 반영하여 배정되는지 테스트")
    void shouldAssignShiftsWhileRespectingDailyUserLeaves() {
        // 11월 2일과 11월 3일에 추가적인 Shift 동적으로 추가
        shifts.addAll(List.of(
                new Shift(4L, "Morning Shift", LocalDate.of(2024, 11, 2), LocalTime.of(9, 0), LocalTime.of(12, 0), null, null),
                new Shift(5L, "Afternoon Shift", LocalDate.of(2024, 11, 2), LocalTime.of(13, 0), LocalTime.of(16, 0), null, null),
                new Shift(6L, "Evening Shift", LocalDate.of(2024, 11, 2), LocalTime.of(17, 0), LocalTime.of(20, 0), null, null),

                new Shift(7L, "Morning Shift", LocalDate.of(2024, 11, 3), LocalTime.of(9, 0), LocalTime.of(12, 0), null, null),
                new Shift(8L, "Afternoon Shift", LocalDate.of(2024, 11, 3), LocalTime.of(13, 0), LocalTime.of(16, 0), null, null),
                new Shift(9L, "Evening Shift", LocalDate.of(2024, 11, 3), LocalTime.of(17, 0), LocalTime.of(20, 0), null, null)
        ));

        // userD를 추가하여 4명으로 확장
        users.add(userD);

        // ✅ 각 사용자가 하루에 한 번씩 휴무를 가지도록 설정
        userLeaveDates.put(userA, Set.of(LocalDate.of(2024, 11, 1)));
        userLeaveDates.put(userB, Set.of(LocalDate.of(2024, 11, 2)));
        userLeaveDates.put(userC, Set.of(LocalDate.of(2024, 11, 3)));
        userLeaveDates.put(userD, Set.of(LocalDate.of(2024, 11, 3))); // userD도 11월 1일 휴무

        // 실행
        autoAssignmentScheduler.assignShifts(new ShiftAssignmentData(shifts, users, userLeaveDates, 2));

        // 모든 Shift가 배정되었는지 확인
        assertThat(shifts).allMatch(shift -> shift.getUser() != null);
        assertThat(shifts.get(0).getUser()).isEqualTo(userB);
        assertThat(shifts.get(1).getUser()).isEqualTo(userC);
        assertThat(shifts.get(2).getUser()).isEqualTo(userD);

        assertThat(shifts.get(3).getUser()).isEqualTo(userA);
        assertThat(shifts.get(4).getUser()).isEqualTo(userC);
        assertThat(shifts.get(5).getUser()).isEqualTo(userD);

        assertThat(shifts.get(6).getUser()).isEqualTo(userA);
        assertThat(shifts.get(7).getUser()).isEqualTo(userB);
        assertThat(shifts.get(8).getUser()).isEqualTo(userA);
    }
}
