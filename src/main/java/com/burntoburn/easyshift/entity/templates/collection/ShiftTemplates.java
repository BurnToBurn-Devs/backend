package com.burntoburn.easyshift.entity.templates.collection;

import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

public class ShiftTemplates {

    // @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    // @JoinColumn(name = "schedule_template_id") // 단방향 연관관계 유지
    // -> OneToMany 쪽에서 연관관계의 주인이 되면 안 됩니다!!

   // @OneToMany(mappedBy = "scheduleTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ShiftTemplate> shiftTemplates = new ArrayList<>();

    // 새로운 ShiftTemplate 목록으로 업데이트
    public void update(List<ShiftTemplate> newShifts) {
        this.shiftTemplates.clear();
        this.shiftTemplates.addAll(newShifts);
    }

    // 새로운 ShiftTemplate 추가
    public void add(ShiftTemplate shiftTemplate) {
        this.shiftTemplates.add(shiftTemplate);
    }

    // ShiftTemplate 여러 개 추가
    public void addAll(List<ShiftTemplate> shiftTemplates) {
        this.shiftTemplates.addAll(shiftTemplates);
    }

    // ShiftTemplate 전체 삭제
    public void clear() {
        this.shiftTemplates.clear();
    }

    // 특정 ID로 ShiftTemplate 찾기
    public ShiftTemplate findById(Long shiftTemplateId) {
        return shiftTemplates.stream()
                .filter(st -> st.getId().equals(shiftTemplateId))
                .findFirst()
                .orElse(null);
    }

    // Getter 추가 (테스트에서 사용 가능하도록)
    public List<ShiftTemplate> getList() {
        return new ArrayList<>(shiftTemplates); // 방어적 복사 (원본 리스트 보호)
    }
}
