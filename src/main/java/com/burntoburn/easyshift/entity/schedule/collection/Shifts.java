package com.burntoburn.easyshift.entity.schedule.collection;

import com.burntoburn.easyshift.entity.schedule.Shift;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//@Embeddable
@NoArgsConstructor
public class Shifts {

    //@OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Shift> shiftList = new ArrayList<>();

    // Shift 추가
    public void add(Shift shift) {
        shiftList.add(shift);
    }

    // 여러 개 Shift 추가
    public void addAll(List<Shift> shifts) {
        shiftList.addAll(shifts);
    }

    // 특정 Shift 삭제
    public void remove(Shift shift) {
        shiftList.remove(shift);
    }

    // 전체 삭제
    public void clear() {
        shiftList.clear();
    }

    // Shift 업데이트 (새로운 Shift 리스트로 교체)
    public void update(List<Shift> newShifts) {
        shiftList.clear();
        shiftList.addAll(newShifts);
    }

    // Shift 리스트 반환 (불변 리스트로 반환하여 외부 수정 방지)
    public List<Shift> getList() {
        return new ArrayList<>(shiftList);
    }
}
