package com.burntoburn.easyshift.repository.schedule;

import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftTemplateRepository extends JpaRepository<ShiftTemplate, Long> {
    Optional<ShiftTemplate> getShiftTemplateById(Long id);
}
