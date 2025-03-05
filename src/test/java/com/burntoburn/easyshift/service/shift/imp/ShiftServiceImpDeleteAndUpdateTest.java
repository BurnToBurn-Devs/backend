package com.burntoburn.easyshift.service.shift.imp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.user.Role;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.exception.shift.ShiftException;
import com.burntoburn.easyshift.exception.user.UserException;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ShiftServiceImpDeleteAndUpdateTest {
    @InjectMocks
    private ShiftServiceImp shiftService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @Test
    @DisplayName("Shift 삭제 성공 테스트")
    void deleteShift_success() {
        Long shiftId = 1L;

        Shift shift = Shift.builder()
                .shiftTemplateId(1L)
                .shiftName("Test")
                .shiftDate(LocalDate.now())
                .startTime(LocalTime.now())
                .endTime(LocalTime.now())
                .build();

        ReflectionTestUtils.setField(shift, "id", shiftId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        // when
        shiftService.deleteShift(shiftId);

        // then
        verify(shiftRepository, times(1)).delete(shift);
    }

    @Test
    @DisplayName("Shift 근무자 변경 성공 테스트")
    void updateUserShift_success() {
        //given
        Long userId = 3L;
        Long shiftId = 1L;

        User user1 = User.builder()
                .id(1L)
                .name("test")
                .role(Role.ADMINISTRATOR)
                .email("asd@aasd.com")
                .phoneNumber("100-1111-111")
                .build();

        User user2 = User.builder()
                .id(userId)
                .name("test")
                .role(Role.ADMINISTRATOR)
                .email("asd@aasd.com")
                .phoneNumber("100-1111-111")
                .build();

        Shift shift = Shift.builder()
                .id(shiftId)
                .user(user1)
                .shiftTemplateId(1L)
                .shiftName("Test")
                .shiftDate(LocalDate.now())
                .startTime(LocalTime.now())
                .endTime(LocalTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        // when
        shiftService.updateUserShift(userId, shiftId);

        // then
        assertEquals(user2.getId(), shift.getUser().getId());  // ✅ ID 값만 비교
        verify(shiftRepository, times(1)).findById(shiftId);
        verify(shiftRepository, times(1)).save(shift);
    }

    @Test
    @DisplayName("Shift 삭제 실패 테스트")
    void deleteShift_shiftNotFound() {
        //given
        Long shiftId = 1L;

        // shiftRepository.findById(shiftId) 빈 리스트 반환
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ShiftException.class, ()->shiftService.deleteShift(shiftId));

        // 삭제 호출 발생 X
        verify(shiftRepository, never()).delete(any(Shift.class));
    }

    @Test
    @DisplayName("Shift 근무자 변경 실패 테스트 - Shift 오류")
    void updateUserShift_shiftNotFound() {
        //given
        Long shiftId = 1L;
        Long userId = 1L;

        User user = User.builder()
                .id(shiftId)
                .name("test")
                .role(Role.ADMINISTRATOR)
                .email("asd@aasd.com")
                .phoneNumber("100-1111-111")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(ShiftException.class, ()->shiftService.updateUserShift(userId, shiftId));

        // 저장 호출 발생 X
        verify(shiftRepository, never()).save(any(Shift.class));
    }

    @Test
    @DisplayName("Shift 근무자 변경 실패 테스트 - User 오류")
    void updateUserShift_userNotFound() {
        //given
        Long shiftId = 1L;
        Long userId = 1L;

        // userRepository 에 userId 가 없을 때
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(UserException.class, ()->shiftService.updateUserShift(userId, shiftId));

        // 저장 호출 발생 X
        verify(shiftRepository, never()).save(any(Shift.class));
    }
}