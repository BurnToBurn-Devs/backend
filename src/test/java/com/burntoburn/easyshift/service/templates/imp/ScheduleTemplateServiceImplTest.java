package com.burntoburn.easyshift.service.templates.imp;

import com.burntoburn.easyshift.dto.template.ScheduleTemplateResponse;
import com.burntoburn.easyshift.dto.template.ScheduleTemplateWithShiftsResponse;
import com.burntoburn.easyshift.dto.template.req.ScheduleTemplateRequest;
import com.burntoburn.easyshift.dto.template.req.ShiftTemplateRequest;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.exception.template.TemplateException;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.service.templates.ScheduleTemplateFactory;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleTemplateServiceImplTest {

    @Mock
    private ScheduleTemplateRepository scheduleTemplateRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ScheduleTemplateFactory scheduleTemplateFactory;

    @InjectMocks
    private ScheduleTemplateServiceImpl scheduleTemplateService;

    // ✅ 1. createScheduleTemplate() 테스트
    @Test
    @DisplayName("Store가 존재하지 않으면 NoSuchElementException 발생")
    void createScheduleTemplate_ShouldThrowException_WhenStoreNotFound() {
        // Given
        Long storeId = 1L;
        ScheduleTemplateRequest request = ScheduleTemplateRequest.builder()
                .scheduleTemplateName("야간 근무")
                .shiftTemplates(List.of(
                        new ShiftTemplateRequest("1교대", LocalTime.of(12, 0), LocalTime.of(15, 0)),
                        new ShiftTemplateRequest("2교대", LocalTime.of(15, 0), LocalTime.of(18, 0))
                ))
                .build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class,
                () -> scheduleTemplateService.createScheduleTemplate(storeId, request));

        verify(storeRepository, times(1)).findById(storeId);
        verify(scheduleTemplateRepository, never()).save(any());
    }

    @Test
    @DisplayName("정상적으로 ScheduleTemplate 생성 후 반환")
    void createScheduleTemplate_ShouldReturnResponse_WhenSuccessful() {
        // Given
        Long storeId = 1L;
        Store store = Store.builder()
                .storeName("test")
                .id(storeId)
                .description("asd")
                .storeCode(UUID.randomUUID())
                .build();

        ScheduleTemplateRequest request = ScheduleTemplateRequest.builder()
                .scheduleTemplateName("야간 근무")
                .shiftTemplates(List.of(
                        new ShiftTemplateRequest("1교대", LocalTime.of(12, 0), LocalTime.of(15, 0)),
                        new ShiftTemplateRequest("2교대", LocalTime.of(15, 0), LocalTime.of(18, 0))
                ))
                .build();
        ScheduleTemplate scheduleTemplate = new ScheduleTemplate(1L, "야간 근무", store);

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(scheduleTemplateFactory.createScheduleTemplate(store, request)).thenReturn(scheduleTemplate);
        when(scheduleTemplateRepository.save(scheduleTemplate)).thenReturn(scheduleTemplate);

        // When
        ScheduleTemplateResponse response = scheduleTemplateService.createScheduleTemplate(storeId, request);

        // Then
        assertNotNull(response);
        assertEquals("야간 근무", response.getScheduleTemplateName());
        verify(storeRepository, times(1)).findById(storeId);
        verify(scheduleTemplateRepository, times(1)).save(scheduleTemplate);
    }

    // ✅ 2. getAllScheduleTemplatesByStore() 테스트
    @Test
    @DisplayName("Store가 존재하지 않으면 NoSuchElementException 발생")
    void getAllScheduleTemplatesByStore_ShouldThrowException_WhenStoreNotFound() {
        // Given
        Long storeId = 1L;
        when(storeRepository.existsById(storeId)).thenReturn(false);

        // When & Then
        assertThrows(NoSuchElementException.class,
                () -> scheduleTemplateService.getAllScheduleTemplatesByStore(storeId));

        verify(scheduleTemplateRepository, never()).findAllByStoreId(any());
    }

    @Test
    @DisplayName("storeId에 해당하는 ScheduleTemplate이 없으면 TemplateException 발생")
    void getAllScheduleTemplatesByStore_ShouldThrowException_WhenNoTemplatesFound() {
        // Given
        Long storeId = 1L;
        when(storeRepository.existsById(storeId)).thenReturn(true);
        when(scheduleTemplateRepository.findAllByStoreId(storeId)).thenReturn(List.of());

        // When & Then
        assertThrows(TemplateException.class,
                () -> scheduleTemplateService.getAllScheduleTemplatesByStore(storeId));

        verify(scheduleTemplateRepository, times(1)).findAllByStoreId(storeId);
    }

    @Test
    @DisplayName("storeId에 해당하는 ScheduleTemplate이 존재하면 정상 반환")
    void getAllScheduleTemplatesByStore_ShouldReturnTemplates_WhenTemplatesExist() {
        // Given
        Long storeId = 1L;
        List<ScheduleTemplate> templates = List.of(new ScheduleTemplate(1L, "야간 근무", null));
        when(storeRepository.existsById(storeId)).thenReturn(true);
        when(scheduleTemplateRepository.findAllByStoreId(storeId)).thenReturn(templates);

        // When
        ScheduleTemplateWithShiftsResponse response = scheduleTemplateService.getAllScheduleTemplatesByStore(storeId);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getScheduleTemplates().size());
        verify(scheduleTemplateRepository, times(1)).findAllByStoreId(storeId);
    }

    // ✅ 3. deleteScheduleTemplate() 테스트
    @Test
    @DisplayName("삭제하려는 ScheduleTemplate이 없으면 TemplateException 발생")
    void deleteScheduleTemplate_ShouldThrowException_WhenTemplateNotFound() {
        // Given
        Long scheduleTemplateId = 1L;
        when(scheduleTemplateRepository.existsById(scheduleTemplateId)).thenReturn(false);

        // When & Then
        assertThrows(TemplateException.class,
                () -> scheduleTemplateService.deleteScheduleTemplate(scheduleTemplateId));

        verify(scheduleTemplateRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("ScheduleTemplate이 존재하면 정상 삭제")
    void deleteScheduleTemplate_ShouldDeleteTemplate_WhenExists() {
        // Given
        Long scheduleTemplateId = 1L;
        when(scheduleTemplateRepository.existsById(scheduleTemplateId)).thenReturn(true);

        // When
        scheduleTemplateService.deleteScheduleTemplate(scheduleTemplateId);

        // Then
        verify(scheduleTemplateRepository, times(1)).deleteById(scheduleTemplateId);
    }
}
