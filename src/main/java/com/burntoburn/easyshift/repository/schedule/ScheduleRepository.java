package com.burntoburn.easyshift.repository.schedule;

import com.burntoburn.easyshift.entity.schedule.Schedule;
import java.time.YearMonth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByStoreId(Long storeId);

    // 특정 매장의 특정 스케줄 조회 (storeId 검증 포함)
    Optional<Schedule> findByIdAndStoreId(Long scheduleId, Long storeId);

    // 스케줄 조회(scheduleId 로)
    @Query("""
            SELECT s FROM Schedule s 
            LEFT JOIN FETCH s.shifts sh
            LEFT JOIN FETCH sh.user u 
            WHERE s.id = :scheduleId
            """)
    Optional<Schedule> findScheduleWithShifts(@Param("scheduleId") Long scheduleId);

    Page<Schedule> findByStoreIdOrderByCreatedAtDesc(Long storeId, Pageable pageable);

    @Query("""
    SELECT s FROM Schedule s
    JOIN FETCH s.store st
    JOIN FETCH s.shifts sh
    JOIN sh.user u
    WHERE st.id = :storeId
    AND s.scheduleMonth = :scheduleMonth
    AND u.id = :userId
    """)
    List<Schedule> findWorkerSchedules(
            @Param("storeId") Long storeId,
            @Param("scheduleMonth") YearMonth scheduleMonth,
            @Param("userId") Long userId
    );

    @Query("""
    SELECT s FROM Schedule s
    WHERE s.scheduleTemplateId = :scheduleTemplateId
    """)
    List<Schedule>findSchedulesWithTemplate(Long scheduleTemplateId);
}
