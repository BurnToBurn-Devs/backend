package com.burntoburn.easyshift.service.shift.imp;

import com.burntoburn.easyshift.dto.shift.req.ShiftUpload;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.exception.shift.ShiftException;
import com.burntoburn.easyshift.exception.user.UserException;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import com.burntoburn.easyshift.service.shift.ShiftService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShiftServiceImp implements ShiftService {
    private final ShiftRepository shiftRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Shift createShift(Long scheduleId, ShiftUpload shiftUpload) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("not found schedule"));

        Shift shift = Shift.builder()
                .shiftName(shiftUpload.getShiftName())
                .shiftDate(shiftUpload.getShiftDate())
                .startTime(shiftUpload.getStartTime())
                .endTime(shiftUpload.getEndTime())
                .schedule(schedule)
                .build();

        return shiftRepository.save(shift);
    }

    public Shift getShiftOne(Long id) {
        return shiftRepository.getShiftById(id)
                .orElseThrow(() -> new NoSuchElementException("Shift not found with id: " + id));
    }


    @Override
    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    @Transactional
    @Override
    public Shift updateShift(Long shiftId, ShiftUpload shiftUpload) {
        // 기존 Shift 찾기 (없으면 예외 발생)
        Shift existingShift = getShiftOne(shiftId);

        // 새로운 값으로 업데이트
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public Shift getShiftWithUser(Long ShiftId) {
        return shiftRepository.findByIdWithUser(ShiftId)
                .orElseThrow(() -> new NoSuchElementException("not found shift"));
    }

    @Override
    @Transactional
    public void deleteShift(Long id) {
        // 먼저 존재하는 Shift인지 확인
        Shift shift = shiftRepository.findById(id).orElseThrow(ShiftException::shiftNotFound);

        // 삭제 수행
        shiftRepository.delete(shift);
    }

    @Override
    @Transactional
    public void updateUserShift(Long userId, Long shiftId) {
        // 교환할 대상 User
        User user = userRepository.findById(userId).orElseThrow(UserException::userNotFound);

        // 교환을 수행하는 Shift
        Shift shift = shiftRepository.findById(shiftId).orElseThrow(ShiftException::shiftNotFound);

        // 교환
        shift.assignUser(user);

        // 명시적으로 save 호출
        shiftRepository.save(shift);
    }
}
