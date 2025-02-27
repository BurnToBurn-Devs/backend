package com.burntoburn.easyshift.service.leave.imp;

import com.burntoburn.easyshift.entity.leave.ApprovalStatus;
import com.burntoburn.easyshift.entity.leave.LeaveRequest;
import com.burntoburn.easyshift.repository.leave.LeaveRequestRepository;
import com.burntoburn.easyshift.service.leave.LeaveRequestAdminService;
import com.burntoburn.easyshift.service.leave.LeaveRequestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class LeaveRequestAdminServiceImpTest {

    @Autowired
    private LeaveRequestAdminService leaveRequestAdminService;

    @MockitoBean
    private LeaveRequestRepository leaveRequestRepository;

    @MockitoBean
    private LeaveRequestFactory leaveRequestFactory;

    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        // 더미 휴가 신청 생성
        leaveRequest = LeaveRequest.builder()
                .id(1L)
                .date(LocalDate.of(2024, 3, 15))
                .approvalStatus(ApprovalStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("휴가 신청 승인 테스트")
    void approveLeaveRequest() {
        // Given
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        doNothing().when(leaveRequestFactory).approvedRequest(leaveRequest);

        // When
        LeaveRequest result = leaveRequestAdminService.approveLeaveRequest(1L);

        // Then
        assertNotNull(result);
        verify(leaveRequestFactory, times(1)).approvedRequest(leaveRequest);
        verify(leaveRequestRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("휴가 신청 거절 테스트")
    void rejectLeaveRequest() {
        // Given
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        doNothing().when(leaveRequestFactory).rejectRequest(leaveRequest);

        // When
        LeaveRequest result = leaveRequestAdminService.rejectLeaveRequest(1L);

        // Then
        assertNotNull(result);
        verify(leaveRequestFactory, times(1)).rejectRequest(leaveRequest);
        verify(leaveRequestRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("특정 월의 휴가 신청 조회 테스트")
    void getLeaveRequestsByMonth() {
        // Given
        YearMonth scheduleMonth = YearMonth.of(2024, 3);

        when(leaveRequestRepository.findAllByScheduleMonth(scheduleMonth)).thenReturn(List.of(leaveRequest));

        // When
        List<LeaveRequest> result = leaveRequestAdminService.getLeaveRequestsByMonth(scheduleMonth);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(leaveRequestRepository, times(1)).findAllByScheduleMonth(scheduleMonth);
    }

    @Test
    @DisplayName("존재하지 않는 휴가 신청 승인 시 예외 발생")
    void approveLeaveRequest_NotFound() {
        // Given
        when(leaveRequestRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> leaveRequestAdminService.approveLeaveRequest(2L));
    }

    @Test
    @DisplayName("존재하지 않는 휴가 신청 거절 시 예외 발생")
    void rejectLeaveRequest_NotFound() {
        // Given
        when(leaveRequestRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> leaveRequestAdminService.rejectLeaveRequest(2L));
    }
}
