package com.burntoburn.easyshift.service.store;

import com.burntoburn.easyshift.dto.store.*;
import com.burntoburn.easyshift.dto.store.use.StoreResponse;
import com.burntoburn.easyshift.dto.store.use.*;
import com.burntoburn.easyshift.dto.user.UserDTO;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.store.UserStore;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.entity.user.Role;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.exception.store.StoreException;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @InjectMocks
    private StoreServiceImpl storeService;
    @Mock
    private UserStoreRepository userStoreRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private ScheduleTemplateRepository scheduleTemplateRepository;
    @Mock
    private ShiftRepository shiftRepository;

    @Test
    @DisplayName("매장 생성 성공 테스트")
    void testCreateStoreSuccess() {
        // given
        StoreCreateRequest request = new StoreCreateRequest();
        ReflectionTestUtils.setField(request, "storeName", "Test Store");
        ReflectionTestUtils.setField(request, "description", "Test Description");

        Store savedStore = Store.builder()
                .storeName(request.getStoreName())
                .storeCode(UUID.randomUUID())
                .description(request.getDescription())
                .build();
        ReflectionTestUtils.setField(savedStore, "id", 1L);

        when(storeRepository.save(any(Store.class))).thenReturn(savedStore);

        // when
        StoreCreateResponse response = storeService.createStore(request);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getStoreId());
        assertEquals("Test Store", response.getStoreName());
        assertNotNull(response.getStoreCode());
    }


    @Test
    @DisplayName("매장 수정 성공 테스트")
    void updateStore_success() {
        // given
        Long storeId = 1L;
        StoreUpdateRequest request = new StoreUpdateRequest("New Store Name", "New Description");

        // 기존 스토어 객체 생성 (필요한 초기값 세팅)
        Store store = Store.builder()
                .storeName("Old Store Name")
                .description("Old Description")
                .build();

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // when
        storeService.updateStore(storeId, request);

        // then: store의 필드가 요청에 맞게 업데이트 되었는지 확인
        assertEquals("New Store Name", store.getStoreName());
        assertEquals("New Description", store.getDescription());
        // findById가 호출된 것을 검증 (추가 검증)
        verify(storeRepository, times(1)).findById(storeId);
    }

    @Test
    @DisplayName("매장 수정 실패 테스트")
    void updateStore_storeNotFound() {
        // given
        Long storeId = 1L;
        StoreUpdateRequest request = new StoreUpdateRequest("New Store Name", "New Description");

        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        // when & then: store가 없으면 StoreException (storeNotFound 예외)가 발생해야 함
        assertThrows(StoreException.class, () -> {
            storeService.updateStore(storeId, request);
        });
    }

    @Test
    @DisplayName("매장 삭제 성공 테스트")
    public void deleteStore_success() {
        // given
        Long storeId = 1L;
        Store store = Store.builder()
                .storeName("Test Store")
                .description("Test Description")
                .build();
        // Builder로 생성한 후, id 필드는 ReflectionTestUtils로 주입합니다.
        ReflectionTestUtils.setField(store, "id", storeId);

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        // when
        storeService.deleteStore(storeId);

        // then
        // storeRepository.delete()가 한 번 호출되었는지 검증
        verify(storeRepository, times(1)).delete(store);
    }

    @Test
    @DisplayName("매장 삭제 실패 테스트 - 매장이 존재하지 않는 경우")
    public void deleteStore_storeNotFound() {
        // given
        Long storeId = 1L;
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        // when & then: 매장이 없으면 StoreException이 발생해야 함
        assertThrows(StoreException.class, () -> storeService.deleteStore(storeId));

        // 삭제 호출은 발생하지 않아야 합니다.
        verify(storeRepository, never()).delete(any(Store.class));
    }

    @Test
    @DisplayName("유저 매장 리스트 성공 테스트")
    public void testGetUserStores_whenStoresExist_returnsUserStoresResponse() {
        // given
        Long userId = 1L;

        // Store 엔티티는 Builder를 통해 생성하지만, id 필드는 @Setter(AccessLevel.NONE)로 되어 있으므로 ReflectionTestUtils를 사용합니다.
        Store store1 = Store.builder()
                .storeName("Store 101")
                .description("Description 101")
                .build();
        ReflectionTestUtils.setField(store1, "id", 101L);

        Store store2 = Store.builder()
                .storeName("Store 201")
                .description("Description 201")
                .build();
        ReflectionTestUtils.setField(store2, "id", 201L);

        List<Store> storeList = Arrays.asList(store1, store2);

        when(userStoreRepository.findStoresByUserId(userId)).thenReturn(storeList);

        // when
        UserStoresResponse response = storeService.getUserStores(userId);

        // then
        assertNotNull(response, "Response should not be null");
        assertEquals(2, response.getStores().size(), "Store list size should be 2");

        // 첫 번째 매장 검증
        StoreResponse storeResponse1 = response.getStores().get(0);
        assertEquals(101L, storeResponse1.getStoreId());
        assertEquals("Store 101", storeResponse1.getStoreName());
        assertEquals("Description 101", storeResponse1.getDescription());

        // 두 번째 매장 검증
        StoreResponse storeResponse2 = response.getStores().get(1);
        assertEquals(201L, storeResponse2.getStoreId());
        assertEquals("Store 201", storeResponse2.getStoreName());
        assertEquals("Description 201", storeResponse2.getDescription());
    }

    @Test
    @DisplayName("유저 매장 빈 리스트 반환 테스트")
    public void testGetUserStores_whenNoStoresExist_returnsEmptyUserStoresResponse() {
        // given
        Long userId = 1L;
        when(userStoreRepository.findStoresByUserId(userId)).thenReturn(Collections.emptyList());

        // when
        UserStoresResponse response = storeService.getUserStores(userId);

        // then
        assertNotNull(response, "Response should not be null even if no stores exist");
        assertTrue(response.getStores().isEmpty(), "Store list should be empty");
    }

    @Test
    @DisplayName("매장 사용자 목록 조회 성공 테스트")
    void getStoreUsers_Success() {
        // given
        Long storeId = 10L;

        Store store = Store.builder()
                .storeName("Test Store")
                .description("Test Description")
                .build();
        ReflectionTestUtils.setField(store, "id", storeId);
        ReflectionTestUtils.setField(store, "storeCode", UUID.fromString("11111111-1111-1111-1111-111111111111"));

        // User 엔티티 목록 생성 (UserDTO가 아닌 User 엔티티)
        List<User> mockUserEntities = List.of(
                User.builder()
                        .id(1L)
                        .name("홍길동")
                        .email("hong@example.com")
                        .phoneNumber("010-1111-2222")
                        .avatarUrl("http://avatar.com/1.png")
                        .role(Role.WORKER)
                        .build(),
                User.builder()
                        .id(2L)
                        .name("김철수")
                        .email("kim@example.com")
                        .phoneNumber("010-3333-4444")
                        .avatarUrl("http://avatar.com/2.png")
                        .role(Role.ADMINISTRATOR)
                        .build()
        );

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(userStoreRepository.findUsersByStoreId(storeId)).thenReturn(mockUserEntities);

        // when
        StoreUsersResponse response = storeService.getStoreUsers(storeId);

        // then
        assertNotNull(response, "Response는 null이 아니어야 함");
        assertEquals(store.getStoreCode(), response.getStoreCode());
        assertEquals(store.getId(), response.getStoreId());
        assertEquals(store.getStoreName(), response.getStoreName());
        assertEquals(store.getDescription(), response.getDescription());
        assertEquals(2, response.getUsers().size(), "사용자 목록 크기는 2여야 함");

        // 내부적으로 User 엔티티를 UserDTO로 변환한 결과를 검증
        UserDTO firstUser = response.getUsers().get(0);
        assertEquals(1L, firstUser.getId());
        assertEquals("홍길동", firstUser.getName());
        assertEquals("hong@example.com", firstUser.getEmail());
        assertEquals("010-1111-2222", firstUser.getPhoneNumber());
        assertEquals("http://avatar.com/1.png", firstUser.getAvatarUrl());
        assertEquals(Role.WORKER, firstUser.getRole());

        UserDTO secondUser = response.getUsers().get(1);
        assertEquals(2L, secondUser.getId());
        assertEquals("김철수", secondUser.getName());
        assertEquals("kim@example.com", secondUser.getEmail());
        assertEquals("010-3333-4444", secondUser.getPhoneNumber());
        assertEquals("http://avatar.com/2.png", secondUser.getAvatarUrl());
        assertEquals(Role.ADMINISTRATOR, secondUser.getRole());

        verify(storeRepository, times(1)).findById(storeId);
        verify(userStoreRepository, times(1)).findUsersByStoreId(storeId);
    }

    @Test
    @DisplayName("매장 사용자 목록 조회 실패 테스트 - 매장 미존재")
    void getStoreUsers_StoreNotFound() {
        // given
        Long storeId = 999L;
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(StoreException.class, () -> storeService.getStoreUsers(storeId));

        // userStoreRepository는 호출되지 않아야 한다
        verify(userStoreRepository, never()).findUsersByStoreId(anyLong());
    }

    @Test
    @DisplayName("매장 사용자 목록 조회 성공 테스트 - 사용자가 없는 경우")
    void getStoreUsers_EmptyUserList() {
        // given
        Long storeId = 10L;

        Store store = Store.builder()
                .storeName("Test Store")
                .description("Test Description")
                .build();
        ReflectionTestUtils.setField(store, "id", storeId);
        ReflectionTestUtils.setField(store, "storeCode", UUID.fromString("11111111-1111-1111-1111-111111111111"));

        // userStoreRepository에서 User 엔티티 리스트를 반환하도록 모킹 (빈 리스트)
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(userStoreRepository.findUsersByStoreId(storeId)).thenReturn(List.of());

        // when
        StoreUsersResponse response = storeService.getStoreUsers(storeId);

        // then
        assertNotNull(response);
        assertEquals(store.getId(), response.getStoreId());
        assertTrue(response.getUsers().isEmpty(), "사용자가 없는 경우 빈 리스트 반환해야 함");

        verify(storeRepository, times(1)).findById(storeId);
        verify(userStoreRepository, times(1)).findUsersByStoreId(storeId);
    }


    @Test
    @DisplayName("매장 정보 조회 성공 테스트 (storeCode)")
    void getStoreSimpleInfo_Success() {
        // given
        UUID storeCode = UUID.fromString("11111111-2222-3333-4444-555555555555");

        Store store = Store.builder()
                .storeName("Test Store")
                .description("This is a test store.")
                .storeCode(storeCode)
                .build();
        ReflectionTestUtils.setField(store, "id", 1L);

        when(storeRepository.findByStoreCode(storeCode)).thenReturn(Optional.of(store));

        // when
        StoreResponse response = storeService.getStoreSimpleInfo(storeCode);

        // then
        assertNotNull(response, "응답이 null이 아니어야 함");
        assertEquals(1L, response.getStoreId());
        assertEquals("Test Store", response.getStoreName());
        assertEquals("This is a test store.", response.getDescription());

        verify(storeRepository, times(1)).findByStoreCode(storeCode);
    }

    @Test
    @DisplayName("매장 정보 조회 실패 테스트 - 매장 없음")
    void getStoreSimpleInfo_StoreNotFound() {
        // given
        UUID storeCode = UUID.fromString("11111111-2222-3333-4444-555555555555");

        when(storeRepository.findByStoreCode(storeCode)).thenReturn(Optional.empty());

        // when & then
        assertThrows(StoreException.class, () -> storeService.getStoreSimpleInfo(storeCode));

        verify(storeRepository, times(1)).findByStoreCode(storeCode);
    }

    @Test
    @DisplayName("스케줄 템플릿이 없는 경우 빈 응답 반환")
    void getStoreInfo_NoScheduleTemplate() {
        // Given
        Long userId = 1L;
        Long storeId = 1L;

        when(userStoreRepository.existsByUserIdAndStoreId(userId, storeId)).thenReturn(true);
        when(scheduleTemplateRepository.findAllWithShiftTemplatesByStoreId(storeId)).thenReturn(Collections.emptyList());

        // When
        StoreInfoResponse response = storeService.getStoreInfo(storeId, userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStoreId()).isEqualTo(storeId);
        assertThat(response.getScheduleTemplates()).isEmpty();
        assertThat(response.getSelectedScheduleTemplate()).isNull();
    }

    @Test
    @DisplayName("매장 접근 권한 없음 예외 발생")
    void getStoreInfo_AccessDenied() {
        // Given
        Long userId = 1L;
        Long storeId = 1L;

        when(userStoreRepository.existsByUserIdAndStoreId(userId, storeId)).thenReturn(false);

        assertThatThrownBy(() -> storeService.getStoreInfo(storeId, userId))
                .isInstanceOf(StoreException.class);
    }

    @Test
    @DisplayName("다양한 스케줄 데이터가 있을 때 응답이 올바르게 변환되는지 확인")
    void getStoreInfo_MultipleSchedules() {
        // Given
        Long userId = 1L;
        Long storeId = 1L;

        ScheduleTemplate st1 = new ScheduleTemplate(101L, "ScheduleTemplateName1", null);
        ScheduleTemplate st2 = new ScheduleTemplate(102L, "ScheduleTemplateName2", null);
        ScheduleTemplate st3 = new ScheduleTemplate(103L, "ScheduleTemplateName3", null);
        List<ScheduleTemplate> scheduleTemplates = List.of(st1, st2, st3);

        // ShiftTemplate
        st1.getShiftTemplates().add(new ShiftTemplate(201L, "오전 근무", LocalTime.of(12, 0), LocalTime.of(15, 0), st1));
        st1.getShiftTemplates().add(new ShiftTemplate(202L, "오후 근무", LocalTime.of(15, 0), LocalTime.of(18, 0), st1));
        st1.getShiftTemplates().add(new ShiftTemplate(203L, "야간 근무", LocalTime.of(18, 0), LocalTime.of(21, 0), st1));

        Shift shift1 = new Shift(201L, "오전 근무", LocalDate.of(2024, 3, 1), null, null, null, null, 201L);
        Shift shift2 = new Shift(202L, "오후 근무", LocalDate.of(2024, 3, 2), null, null, null, null, 202L);
        Shift shift3 = new Shift(203L, "야간 근무", LocalDate.of(2024, 3, 2), null, null, null, null, 203L);
        List<Shift> shifts = List.of(shift1, shift2, shift3);

        when(userStoreRepository.existsByUserIdAndStoreId(userId, storeId)).thenReturn(true);
        when(scheduleTemplateRepository.findAllWithShiftTemplatesByStoreId(storeId)).thenReturn(scheduleTemplates);
        when(shiftRepository.findAllByScheduleTemplateIdAndDateBetween(eq(storeId), any(), any(), any())).thenReturn(shifts);

        // When
        StoreInfoResponse response = storeService.getStoreInfo(storeId, userId);

        // Then
        assertThat(response)
                .isNotNull()
                .extracting(StoreInfoResponse::getStoreId)
                .isEqualTo(storeId);

        assertThat(response.getScheduleTemplates())
                .hasSize(3)
                .extracting(ScheduleTemplateDto::getScheduleTemplateId, ScheduleTemplateDto::getScheduleTemplateName)
                .containsExactlyInAnyOrder(
                        tuple(101L, "ScheduleTemplateName1"),
                        tuple(102L, "ScheduleTemplateName2"),
                        tuple(103L, "ScheduleTemplateName3")
                );

        assertThat(response.getSelectedScheduleTemplate())
                .isNotNull()
                .extracting(SelectedScheduleTemplateDto::getScheduleTemplateId, SelectedScheduleTemplateDto::getScheduleTemplateName)
                .containsExactly(101L, "ScheduleTemplateName1");

        assertThat(response.getSelectedScheduleTemplate().getShifts())
                .hasSize(3)
                .extracting(ShiftTemplateDto::getShiftTemplateId, ShiftTemplateDto::getShiftTemplateName, ShiftTemplateDto::getStartTime, ShiftTemplateDto::getEndTime)
                .containsExactlyInAnyOrder(
                        tuple(201L, "오전 근무", "12:00", "15:00"),
                        tuple(202L, "오후 근무", "15:00", "18:00"),
                        tuple(203L, "야간 근무", "18:00", "21:00")
                );
    }

    @Test
    @DisplayName("매장 입장 성공 테스트")
    void joinUserStore_Success() {
        // given
        UUID storeCode = UUID.fromString("11111111-2222-3333-4444-555555555555");
        Long userId = 1L;

        Store store = Store.builder()
                .id(10L)
                .storeName("Test Store")
                .storeCode(storeCode)
                .description("테스트 매장")
                .build();

        User user = User.builder()
                .id(userId)
                .name("홍길동")
                .email("hong@example.com")
                .build();

        when(storeRepository.findByStoreCode(storeCode)).thenReturn(Optional.of(store));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userStoreRepository.existsByUserIdAndStoreId(userId, store.getId())).thenReturn(false);
        when(userStoreRepository.save(any(UserStore.class))).thenReturn(
                UserStore.builder()
                        .user(user)
                        .store(store)
                        .build()
        );

        // when
        storeService.joinUserStore(storeCode, userId);

        // then
        verify(storeRepository, times(1)).findByStoreCode(storeCode);
        verify(userRepository, times(1)).findById(userId);
        verify(userStoreRepository, times(1)).existsByUserIdAndStoreId(userId, store.getId());
        verify(userStoreRepository, times(1)).save(any(UserStore.class));
    }

    @Test
    @DisplayName("매장 입장 실패 테스트 - 이미 가입된 경우")
    void joinUserStore_AlreadyJoined() {
        // given
        UUID storeCode = UUID.fromString("11111111-2222-3333-4444-555555555555");
        Long userId = 1L;

        Store store = Store.builder()
                .id(10L)
                .storeName("Test Store")
                .storeCode(storeCode)
                .description("테스트 매장")
                .build();

        User user = User.builder()
                .id(userId)
                .name("홍길동")
                .email("hong@example.com")
                .build();

        when(storeRepository.findByStoreCode(storeCode)).thenReturn(Optional.of(store));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userStoreRepository.existsByUserIdAndStoreId(userId, store.getId())).thenReturn(true);

        // when & then
        assertThrows(StoreException.class, () -> storeService.joinUserStore(storeCode, userId));

        verify(storeRepository, times(1)).findByStoreCode(storeCode);
        verify(userRepository, times(1)).findById(userId);
        verify(userStoreRepository, times(1)).existsByUserIdAndStoreId(userId, store.getId());
        verify(userStoreRepository, never()).save(any(UserStore.class));
    }

}