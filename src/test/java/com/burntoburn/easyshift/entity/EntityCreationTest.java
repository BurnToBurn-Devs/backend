package com.burntoburn.easyshift.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.leave.LeaveRequestRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import java.time.YearMonth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Transactional
@SpringBootTest
class EntityCreationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private StoreRepository storeRepository;
    @Autowired private ScheduleRepository scheduleRepository;
    @Autowired private ShiftRepository shiftRepository;
    @Autowired private LeaveRequestRepository leaveRequestRepository;

    private Store store;
    private User user;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        store = storeRepository.saveAndFlush(Store.builder()
                .storeName("Test Store")
                .storeCode(UUID.randomUUID())
                .build());

        user = userRepository.saveAndFlush(User.builder()
                .email("test@example.com")
                .phoneNumber("010-1234-5678")
                .role(com.burntoburn.easyshift.entity.user.Role.WORKER)
                .avatarUrl("https://example.com/avatar.png")
                .build());

        schedule = scheduleRepository.save(Schedule.builder()
                .scheduleName("Test Schedule")
                .scheduleMonth(YearMonth.of(2024, 11))
                .scheduleStatus(com.burntoburn.easyshift.entity.schedule.ScheduleStatus.PENDING)
                .store(store)
                .build());
    }

    @Test
    @DisplayName("User 엔티티 생성 시 createdAt 자동 설정 확인")
    void shouldSetCreatedAtForUser() {
        assertNotNull(user.getCreatedAt(), "❌ createdAt이 설정되지 않았습니다.");
        System.out.println("User CreatedAt: " + user.getCreatedAt());
    }

    @Test
    @DisplayName("Store 엔티티 생성 시 createdAt 자동 설정 확인")
    void shouldSetCreatedAtForStore() {
        assertNotNull(store.getCreatedAt(), "❌ createdAt이 설정되지 않았습니다.");
        System.out.println("Store CreatedAt: " + store.getCreatedAt());
    }

    @Test
    @DisplayName("Schedule 엔티티 생성 시 createdAt 자동 설정 확인")
    void shouldSetCreatedAtForSchedule() {
        assertNotNull(schedule.getCreatedAt(), "❌ createdAt이 설정되지 않았습니다.");
        System.out.println("Schedule CreatedAt: " + schedule.getCreatedAt());
    }

    @Test
    @DisplayName("Shift 엔티티 생성 시 createdAt 자동 설정 확인")
    void shouldSetCreatedAtForShift() {
        Shift shift = shiftRepository.save(Shift.builder()
                .shiftName("Morning Shift")
                .shiftDate(LocalDate.now())
                .startTime(LocalDate.now().atTime(9, 0).toLocalTime())
                .endTime(LocalDate.now().atTime(13, 0).toLocalTime())
                .schedule(schedule)
                .user(user)
                .build());

        assertNotNull(shift.getCreatedAt(), "❌ createdAt이 설정되지 않았습니다.");
        System.out.println("Shift CreatedAt: " + shift.getCreatedAt());
    }

    @Test
    @DisplayName("LeaveRequest 엔티티 생성 시 createdAt 자동 설정 확인")
    void shouldSetCreatedAtForLeaveRequest() {
        LeaveRequest leaveRequest = leaveRequestRepository.save(LeaveRequest.builder()
                .date(LocalDate.now())
                .approvalStatus(com.burntoburn.easyshift.entity.user.ApprovalStatus.PENDING)
                .user(user)
                .schedule(schedule)
                .build());

        assertNotNull(leaveRequest.getCreatedAt(), "❌ createdAt이 설정되지 않았습니다.");
        System.out.println("LeaveRequest CreatedAt: " + leaveRequest.getCreatedAt());
    }
}
