package com.burntoburn.easyshift.service.leave.imp;

import com.burntoburn.easyshift.dto.leave.req.LeaveRequestDto;
import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.exception.leave.LeaveException;
import com.burntoburn.easyshift.repository.leave.LeaveRequestRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import com.burntoburn.easyshift.service.leave.LeaveRequestFactory;
import com.burntoburn.easyshift.service.leave.LeaveRequestWorkerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class LeaveRequestWorkerServiceImpTest {

    @Autowired
    private LeaveRequestWorkerService leaveRequestWorkerService;

    @MockitoBean
    private LeaveRequestRepository leaveRequestRepository;

    @MockitoBean
    private ScheduleRepository scheduleRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private LeaveRequestFactory leaveRequestFactory;

    private User user;
    private Schedule schedule;
    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();

        schedule = Schedule.builder()
                .id(1L)
                .build();

        leaveRequest = LeaveRequest.builder()
                .id(1L)
                .user(user)
                .schedule(schedule)
                .date(LocalDate.of(2024, 3, 10))
                .build();
    }

    @Test
    @DisplayName("휴무 신청 생성 테스트")
    void createLeaveRequest() {
        // Given
        LeaveRequestDto requestDto = LeaveRequestDto.builder()
                .dates(List.of(LocalDate.of(2024, 3, 10)))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(leaveRequestFactory.createLeaveRequest(user, schedule, requestDto.getDates().get(0))).thenReturn(leaveRequest);
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);

        // When
        leaveRequestWorkerService.createLeaveRequest(1L, 1L, requestDto);

        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
    }

    @Test
    @DisplayName("휴무 신청 단건 조회 테스트")
    void getLeaveRequest() {
        // Given
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        // When
        LeaveRequest foundLeaveRequest = leaveRequestWorkerService.getLeaveRequest(1L);

        // Then
        assertNotNull(foundLeaveRequest);
        assertEquals(leaveRequest.getId(), foundLeaveRequest.getId());

        verify(leaveRequestRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("사용자의 모든 휴무 신청 조회 테스트")
    void getLeaveRequestsByUser() {
        // Given
        when(leaveRequestRepository.findAllByUserId(1L)).thenReturn(List.of(leaveRequest));

        // When
        List<LeaveRequest> leaveRequests = leaveRequestWorkerService.getLeaveRequestsByUser(1L);

        // Then
        assertNotNull(leaveRequests);
        assertEquals(1, leaveRequests.size());
        assertEquals(leaveRequest.getId(), leaveRequests.get(0).getId());

        verify(leaveRequestRepository, times(1)).findAllByUserId(1L);
    }

    @Test
    @DisplayName("휴무 신청 업데이트 테스트 - 명시적 저장 방식")
    void updateLeaveRequest() {
        // Given
        LocalDate newDate = LocalDate.of(2024, 3, 15);
        LeaveRequest updatedLeaveRequest = LeaveRequest.builder()
                .id(1L)
                .user(user)
                .schedule(schedule)
                .date(newDate)
                .build();

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(updatedLeaveRequest);

        // When
        LeaveRequest result = leaveRequestWorkerService.updateLeaveRequest(1L, newDate);

        // Then
        assertNotNull(result, "업데이트된 휴무 요청이 null이면 안 됩니다.");
        assertEquals(newDate, result.getDate(), "휴무 요청 날짜가 변경되지 않았습니다.");

        // ✅ save()가 정확히 호출되었는지 검증
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
    }


    @Test
    @DisplayName("휴무 신청 취소 테스트")
    void cancelLeaveRequest() {
        // Given
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        doNothing().when(leaveRequestRepository).deleteById(1L);

        // When
        leaveRequestWorkerService.cancelLeaveRequest(1L, 1L);

        // Then
        verify(leaveRequestRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 휴무 신청 조회 시 예외 발생")
    void getLeaveRequest_NotFound() {
        // Given
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(LeaveException.class, () -> leaveRequestWorkerService.getLeaveRequest(1L));
    }

    @Test
    @DisplayName("존재하지 않는 휴무 신청 삭제 시 예외 발생")
    void cancelLeaveRequest_NotFound() {
        // Given
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(LeaveException.class, () -> leaveRequestWorkerService.cancelLeaveRequest(1L, 1L));
    }
}
