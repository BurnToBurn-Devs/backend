package com.burntoburn.easyshift.entity.leave;

import com.burntoburn.easyshift.entity.BaseEntity;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "leave_request") // 테이블명 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자 (protected)
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
@Builder // Lombok Builder 적용
public class LeaveRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // ID는 자동 생성되므로 Builder에서 제외
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus; // PENDING, APPROVED, REJECTED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    // 명시적으로 Update 매서드 추가
    public LeaveRequest updateDate(LocalDate newDate) {
        this.date = newDate;
        return this;
    }

    public void approvedRequest() {
        this.approvalStatus = ApprovalStatus.APPROVED;
    }

    public void rejectRequest() {
        this.approvalStatus = ApprovalStatus.REJECTED;
    }
}
