package com.burntoburn.easyshift.service.templates;

import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import java.util.List;

public interface ShiftTemplateService {
    // ShiftTemplate 생성
    ShiftTemplate createShiftTemplate(ShiftTemplate shiftTemplate);

    // ShiftTemplate 조회 - 단건
    ShiftTemplate getShiftTemplateOne(Long id);

    // ShiftTemplate 조회 - 전체
    List<ShiftTemplate> getAllShiftTemplates();

    // ShiftTemplate 수정
    ShiftTemplate updateShiftTemplate(Long id, ShiftTemplate shiftTemplate);

    // ShiftTemplate 삭제
    void deleteShiftTemplate(Long id);
}
