package com.burntoburn.easyshift.service.templates.imp;

import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.repository.schedule.ShiftTemplateRepository;
import com.burntoburn.easyshift.service.templates.ShiftTemplateService;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShiftTemplateServiceImp implements ShiftTemplateService {
    private final ShiftTemplateRepository shiftTemplateRepository;

    @Override
    public ShiftTemplate createShiftTemplate(ShiftTemplate shiftTemplate) {
        return shiftTemplateRepository.save(shiftTemplate);
    }

    @Override
    public ShiftTemplate getShiftTemplateOne(Long id) {
        return shiftTemplateRepository.getShiftTemplateById(id)
                .orElseThrow(() -> new NoSuchElementException("ShiftTemplate not found with id: " + id));
    }

    @Override
    public List<ShiftTemplate> getAllShiftTemplates() {
        return shiftTemplateRepository.findAll();
    }

    @Override
    public ShiftTemplate updateShiftTemplate(Long id, ShiftTemplate shiftTemplate) {
        ShiftTemplate existingTemplate = getShiftTemplateOne(id);

        existingTemplate = ShiftTemplate.builder()
                .id(existingTemplate.getId()) // 기존 ID 유지
                .shiftTemplateName(shiftTemplate.getShiftTemplateName())
                .startTime(shiftTemplate.getStartTime())
                .endTime(shiftTemplate.getEndTime())
                .build();

        return shiftTemplateRepository.save(existingTemplate);
    }

    @Override
    public void deleteShiftTemplate(Long id) {
        getShiftTemplateOne(id); // 존재하는지 확인
        shiftTemplateRepository.deleteById(id);
    }
}
