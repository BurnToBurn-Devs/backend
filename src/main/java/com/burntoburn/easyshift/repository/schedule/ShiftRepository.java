package com.burntoburn.easyshift.repository.schedule;

import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Optional<Shift> getShiftById(Long id);

    // Shift 엔티티와 연결된 User 정보를 패치 조인
    @Query("SELECT s FROM Shift s LEFT JOIN FETCH s.user WHERE s.id = :id")
    Optional<Shift> findByIdWithUser(@Param("id") Long id);

    List<Shift> findAllBySchedule(Schedule schedule);

    /**
     * 특정 매장(Store)에서 지정된 스케줄 템플릿(ScheduleTemplate)에 기반하여 생성된 Shift 목록 조회 (기간 필터링 포함)
     * - 특정 매장(storeId)에 속한 Shift 중
     * - 해당 Shift가 속한 Schedule의 ScheduleTemplate ID가 templateId와 일치하는 경우
     * - Shift의 날짜(shiftDate)가 startDate와 endDate 사이(포함)인 경우 조회
     * - Shift에 배정된 User 정보를 LEFT JOIN FETCH하여 함께 조회
     */
    @Query("""
           SELECT sh FROM Shift sh 
            JOIN FETCH sh.schedule sch 
            JOIN FETCH sch.store st  
            LEFT JOIN FETCH sh.user u 
            WHERE st.id = :storeId 
            AND sch.scheduleTemplateId = :templateId 
            AND sh.shiftDate BETWEEN :startDate AND :endDate
            """)
    List<Shift> findAllByScheduleTemplateIdAndDateBetween(
            @Param("storeId") Long storeId,
            @Param("templateId") Long templateId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
            SELECT sh FROM Shift sh 
            JOIN FETCH sh.schedule sch 
            LEFT JOIN FETCH sh.user u 
            WHERE sch.id = :scheduleId 
            """)
    List<Shift> findAllByScheduleId(@Param("scheduleId") Long scheduleId);


    @Query("""
            SELECT sh FROM Shift sh 
            JOIN FETCH sh.user u
            WHERE sh.schedule.id IN :scheduleIds 
            AND sh.shiftDate BETWEEN :startDate AND :endDate
        """)
    List<Shift> findShiftsByScheduleIdWithUser(@Param("scheduleIds") List<Long> scheduleIds,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
}
