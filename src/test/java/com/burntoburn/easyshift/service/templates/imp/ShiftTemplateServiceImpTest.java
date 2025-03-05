package com.burntoburn.easyshift.service.templates.imp;

import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.repository.schedule.ShiftTemplateRepository;
import com.burntoburn.easyshift.service.templates.ShiftTemplateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ShiftTemplateServiceImpTest {

    @Autowired
    private ShiftTemplateService shiftTemplateService; // ✅ 실제 서비스 객체

    @MockitoBean
    private ShiftTemplateRepository shiftTemplateRepository; // ✅ Mock Repository (Spring Context 내에서 동작)

    @Test
    @DisplayName("쉬프트 템플릿 생성")
    void createShiftTemplate() {
        // Given
        ShiftTemplate newShiftTemplate = ShiftTemplate.builder()
                .shiftTemplateName("Evening Shift")
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(22, 0))
                .build();

        // Mock 설정: save()가 호출되면 newShiftTemplate 반환
        when(shiftTemplateRepository.save(any(ShiftTemplate.class))).thenReturn(newShiftTemplate);

        // When
        ShiftTemplate createdTemplate = shiftTemplateService.createShiftTemplate(newShiftTemplate);

        // Then
        assertNotNull(createdTemplate);
        assertEquals("Evening Shift", createdTemplate.getShiftTemplateName());
        verify(shiftTemplateRepository, times(1)).save(any(ShiftTemplate.class));
    }

    @Test
    @DisplayName("쉬프트 템플릿 조회 - 단건")
    void getShiftTemplateOne() {
        // Given
        ShiftTemplate shiftTemplate = ShiftTemplate.builder()
                .id(1L)
                .shiftTemplateName("Morning Shift")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        when(shiftTemplateRepository.getShiftTemplateById(anyLong())).thenReturn(Optional.of(shiftTemplate));

        // When
        ShiftTemplate foundTemplate = shiftTemplateService.getShiftTemplateOne(1L);

        // Then
        assertNotNull(foundTemplate);
        assertEquals(1L, foundTemplate.getId());
        assertEquals("Morning Shift", foundTemplate.getShiftTemplateName());
        verify(shiftTemplateRepository, times(1)).getShiftTemplateById(anyLong());
    }

    @Test
    @DisplayName("쉬프트 템플릿 조회 - 전체")
    void getAllShiftTemplates() {
        // Given
        List<ShiftTemplate> templateList = Arrays.asList(
                ShiftTemplate.builder()
                        .shiftTemplateName("Morning Shift")
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(17, 0))
                        .build(),
                ShiftTemplate.builder()
                        .shiftTemplateName("Afternoon Shift")
                        .startTime(LocalTime.of(13, 0))
                        .endTime(LocalTime.of(21, 0))
                        .build()
        );

        when(shiftTemplateRepository.findAll()).thenReturn(templateList);

        // When
        List<ShiftTemplate> templates = shiftTemplateService.getAllShiftTemplates();

        // Then
        assertNotNull(templates);
        assertEquals(2, templates.size());
        assertEquals("Morning Shift", templates.get(0).getShiftTemplateName());
        assertEquals("Afternoon Shift", templates.get(1).getShiftTemplateName());
        verify(shiftTemplateRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("쉬프트 템플릿 업데이트")
    void updateShiftTemplate() {
        // Given
        ShiftTemplate existingTemplate = ShiftTemplate.builder()
                .id(1L)
                .shiftTemplateName("Morning Shift")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        ShiftTemplate updatedTemplateDetails = ShiftTemplate.builder()
                .shiftTemplateName("Evening Shift")
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(22, 0))
                .build();

        when(shiftTemplateRepository.getShiftTemplateById(1L)).thenReturn(Optional.of(existingTemplate));
        when(shiftTemplateRepository.save(any(ShiftTemplate.class))).thenReturn(updatedTemplateDetails);

        // When
        ShiftTemplate updatedTemplate = shiftTemplateService.updateShiftTemplate(1L, updatedTemplateDetails);

        // Then
        assertNotNull(updatedTemplate);
        assertEquals("Evening Shift", updatedTemplate.getShiftTemplateName());
        verify(shiftTemplateRepository, times(1)).getShiftTemplateById(1L);
        verify(shiftTemplateRepository, times(1)).save(any(ShiftTemplate.class));
    }

    @Test
    @DisplayName("쉬프트 템플릿 삭제")
    void deleteShiftTemplate() {
        // Given
        Long templateId = 1L;
        ShiftTemplate templateToDelete = ShiftTemplate.builder()
                .id(templateId)
                .shiftTemplateName("Night Shift")
                .startTime(LocalTime.of(21, 0))
                .endTime(LocalTime.of(5, 0))
                .build();

        // Mock 설정: 삭제할 객체 조회 가능하도록 설정
        when(shiftTemplateRepository.getShiftTemplateById(templateId)).thenReturn(Optional.of(templateToDelete));

        // Mock 설정: deleteById() 호출 시 아무 동작도 하지 않도록 설정
        doNothing().when(shiftTemplateRepository).deleteById(templateId);

        // When
        shiftTemplateService.deleteShiftTemplate(templateId);

        // Then
        verify(shiftTemplateRepository, times(1)).getShiftTemplateById(templateId);
        verify(shiftTemplateRepository, times(1)).deleteById(templateId);

        // 삭제 후 findById()가 Optional.empty()를 반환하는지 검증
        when(shiftTemplateRepository.findById(templateId)).thenReturn(Optional.empty());
        Optional<ShiftTemplate> deletedTemplate = shiftTemplateRepository.findById(templateId);
        assertTrue(deletedTemplate.isEmpty(), "삭제된 ShiftTemplate이 더 이상 존재하지 않아야 합니다.");
    }
}
