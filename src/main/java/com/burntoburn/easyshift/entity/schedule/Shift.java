package com.burntoburn.easyshift.entity.schedule;

import com.burntoburn.easyshift.entity.BaseEntity;
import com.burntoburn.easyshift.entity.user.User;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@Table(name = "shift") // 테이블명 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자 (protected)
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
@Builder // Lombok Builder 적용
public class Shift extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // ID는 자동 생성되므로 Builder에서 제외
    @Column(name = "shift_id")
    private Long id;

    @Column(nullable = false)
    private String shiftName;

    @Column(nullable = true)
    private LocalDate shiftDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 초기 스케줄 생성시 user 정보는 음
    private User user;
    private Long shiftTemplateId;

    public void updateShift(String shiftName, LocalDate shiftDate, LocalTime startTime, LocalTime endTime) {
        this.shiftName = shiftName;
        this.shiftDate = shiftDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void assignUser(User user) {
        this.user = user;
    }
}
