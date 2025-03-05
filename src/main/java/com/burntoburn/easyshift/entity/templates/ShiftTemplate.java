package com.burntoburn.easyshift.entity.templates;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Entity
@Table(name = "shift_template") // 테이블명 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder // Lombok Builder 적용
public class ShiftTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "shift_template_id")
    private Long id;

    @Column(nullable = false)
    private String shiftTemplateName;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_template_id", nullable = false)
    private ScheduleTemplate scheduleTemplate;
}
