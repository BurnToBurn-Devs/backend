package com.burntoburn.easyshift.entity.store;

import com.burntoburn.easyshift.entity.BaseEntity;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "store") // 테이블명 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder // Lombok Builder 적용
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // id는 Builder에서 설정 불가
    @Column(name = "store_id")
    private Long id;

    @Column(nullable = false)
    private String storeName;

    @Column(unique = true, nullable = false)
    private UUID storeCode;
    private String description;

    // 매장에 속한 스케줄 목록
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules;

    // Store와 연결된 UserStore 목록 (양방향 연관관계)
    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserStore> userStores;

    @PrePersist
    private void initStoreCode() {
        this.storeCode = UUID.randomUUID();
    }

    // 매장 이름 변경하는 메서드
    public void updateStoreName(String newName) {
        this.storeName = newName;
    }
}
