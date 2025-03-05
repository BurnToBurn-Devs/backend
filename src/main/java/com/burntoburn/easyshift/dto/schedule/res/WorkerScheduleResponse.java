package com.burntoburn.easyshift.dto.schedule.res;

import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.store.Store;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkerScheduleResponse {
    private StoreInfo store;
    private List<WorkerSchedule> schedules;

    public static WorkerScheduleResponse fromEntity(Store store, List<Schedule> schedules) {
        List<WorkerSchedule> scheduleDTOs = schedules.stream()
                .map(WorkerSchedule::fromEntity)
                .toList();
        StoreInfo storeInfo = StoreInfo.fromEntity(store);
        return new WorkerScheduleResponse(storeInfo, scheduleDTOs);
    }
}
