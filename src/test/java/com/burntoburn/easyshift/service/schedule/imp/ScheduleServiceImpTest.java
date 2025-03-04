package com.burntoburn.easyshift.service.schedule.imp;



import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;

import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.service.schedule.ScheduleFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ScheduleServiceImpTest {

    @InjectMocks
    private ScheduleServiceImp scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private ScheduleTemplateRepository scheduleTemplateRepository;
    @Mock
    private ShiftRepository shiftRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private ScheduleFactory scheduleFactory;

    private Store store;
    private ScheduleTemplate scheduleTemplate;
    private Schedule schedule;
    private Shift shift;


    @Test
    @DisplayName("스케줄 생성 테스트 - 빈 Shift 포함 검증")
    void createScheduleWithShifts() {
        // Given
        int daysInMonth = YearMonth.of(2024, 3).lengthOfMonth(); // 한 달의 일 수 (3월 → 31일)

        List<ShiftRequest> shiftRequests = List.of(
                new ShiftRequest(1L, 3), // ✅ Morning Shift에 3명 배정
                new ShiftRequest(2L, 2)  // ✅ Evening Shift에 2명 배정
        );

        ScheduleRequest request = ScheduleRequest.builder()
                .scheduleName("March Schedule")
                .scheduleMonth(YearMonth.of(2024, 3))
                .scheduleTemplateId(1L)
                .shiftDetails(shiftRequests) // ✅ ShiftRequest 추가
                .build();

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(scheduleTemplateRepository.findById(1L)).thenReturn(Optional.of(scheduleTemplate));
        when(scheduleRepository.saveAndFlush(any(Schedule.class))).thenReturn(existingSchedule);

        // When
        Schedule createdSchedule = scheduleService.createSchedule(1L, request);

        // Then
        assertNotNull(createdSchedule, "생성된 Schedule이 null이면 안 됩니다.");
        assertEquals("March Schedule", createdSchedule.getScheduleName());
        assertEquals(YearMonth.of(2024, 3), createdSchedule.getScheduleMonth());

        // ✅ 올바른 Shift 개수 계산
        int expectedShifts = shiftRequests.stream()
                .mapToInt(shiftRequest -> shiftRequest.getExpectedWorkers() * daysInMonth) // 각 ShiftTemplate에 대해 한 달 동안 생성된 Shift 개수
                .sum();

        assertNotNull(createdSchedule.getShifts(), "Shifts 객체가 null이면 안 됩니다.");
        assertFalse(createdSchedule.getShifts().isEmpty(), "Shift 리스트가 비어있으면 안 됩니다.");
        assertEquals(expectedShifts, createdSchedule.getShifts().size(), "Shift 개수가 일치해야 합니다.");

        // 변경 감지 방식을 사용하는 경우 shiftRepository.saveAll() 호출이 발생하지 않으므로 아래 검증을 주석 처리합니다.
        // verify(shiftRepository, times(1)).saveAll(anyList());

        // ✅ Schedule 저장 여부 검증
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }


    @Test
    @DisplayName("스케줄 삭제 테스트")
    void deleteSchedule() {
        // Given
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(existingSchedule));
        doNothing().when(scheduleRepository).delete(existingSchedule);

        // When
        scheduleService.deleteSchedule(1L);

        // Then
        verify(scheduleRepository, times(1)).delete(existingSchedule);
    }

    @Test
    @DisplayName("스케줄 수정 테스트")
    void updateSchedule() {
        // Given
        ScheduleRequest request = ScheduleRequest.builder()
                .scheduleName("Updated March Schedule")
                .scheduleMonth(YearMonth.of(2024, 3))
                .build();

        Schedule updatedSchedule = Schedule.builder()
                .id(1L)
                .scheduleName("Updated March Schedule")
                .scheduleMonth(YearMonth.of(2024, 3))
                .scheduleStatus(ScheduleStatus.PENDING)
                .store(store)
                .build();

        // storeId와 scheduleId를 함께 조회하도록 스텁 설정
        when(scheduleRepository.findByIdAndStoreId(1L, 1L)).thenReturn(Optional.of(existingSchedule));
        // 변경 감지를 통해 업데이트를 진행하므로, save 호출은 발생하지 않습니다.
        // when(scheduleRepository.save(any(Schedule.class))).thenReturn(updatedSchedule);

        // When: 올바른 파라미터(1L, 1L, request)로 메서드 호출
        Schedule result = scheduleService.updateSchedule(1L, 1L, request);

        // Then
        assertNotNull(result);
        assertEquals("Updated March Schedule", result.getScheduleName());

        // scheduleRepository.save(...) 호출 검증 제거
        // verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }
    @Test
    @DisplayName("존재하지 않는 스케줄 삭제 시 예외 발생")
    void deleteSchedule_NotFound() {
        // Given
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> scheduleService.deleteSchedule(1L));
    }

    @Test
    @DisplayName("존재하지 않는 스케줄 수정 시 예외 발생")
    void updateSchedule_NotFound() {
        // Given
        ScheduleRequest request = ScheduleRequest.builder()
                .scheduleName("Updated Schedule")
                .scheduleMonth(YearMonth.of(2024, 3))
                .build();

        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> scheduleService.updateSchedule(1L, 1L,request));
    }
}
