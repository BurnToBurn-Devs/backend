package com.burntoburn.easyshift.service.store;


import com.burntoburn.easyshift.dto.store.use.*;
import com.burntoburn.easyshift.entity.store.Store;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class StoreQueryTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStoreRepository userStoreRepository;

    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;


    @Autowired
    private ShiftRepository shiftRepository;
    @Test
    @DisplayName("매장 생성 쿼리 확인")
    void createStoreQueryTest(){
        // given
        StoreCreateRequest request = new StoreCreateRequest();
        request.setStoreName("Test Store");
        request.setDescription("Test Store Description");

        // when
        StoreCreateResponse response = storeService.createStore(request);

        // then
        assertNotNull(response);
        Long storeId = response.getStoreId();
        Optional<Store> storeOpt = storeRepository.findById(storeId);
        assertTrue(storeOpt.isPresent(), "저장된 매장을 조회할 수 있어야 합니다.");

        Store store = storeOpt.get();
        assertEquals("Test Store", store.getStoreName());
        assertEquals("Test Store Description", store.getDescription());
    }

    @Test
    @DisplayName("매장 수정 쿼리 확인")
    void testUpdateStoreQueryExecution() {
        // given: 먼저 매장을 생성
        StoreCreateRequest createRequest = new StoreCreateRequest();
        createRequest.setStoreName("Original Store Name");
        createRequest.setDescription("Original Description");
        StoreCreateResponse createResponse = storeService.createStore(createRequest);
        Long storeId = createResponse.getStoreId();

        // when: 매장 정보 수정
        StoreUpdateRequest updateRequest = new StoreUpdateRequest("Updated Store Name", "Updated Description");
        storeService.updateStore(storeId, updateRequest);
        storeRepository.flush();

        // then: 수정된 매장을 조회하여 검증
        Store updatedStore = storeRepository.findById(storeId).orElseThrow();
        assertEquals("Updated Store Name", updatedStore.getStoreName());
        assertEquals("Updated Description", updatedStore.getDescription());
        // ※ 콘솔 로그에 update 쿼리가 출력됩니다.
    }

    @Test
    @DisplayName("매장 삭제 쿼리 확인")
    void testDeleteStoreQueryExecution() {
        // given: 삭제할 매장을 생성
        StoreCreateRequest createRequest = new StoreCreateRequest();
        createRequest.setStoreName("Store To Delete");
        createRequest.setDescription("Store To Delete Description");
        StoreCreateResponse createResponse = storeService.createStore(createRequest);
        Long storeId = createResponse.getStoreId();

        // when: 매장 삭제
        storeService.deleteStore(storeId);
        storeRepository.flush();

        // then: 삭제된 매장을 조회할 때 null이어야 함
        Optional<Store> storeOpt = storeRepository.findById(storeId);
        assertFalse(storeOpt.isPresent(), "매장이 삭제되어 조회되지 않아야 합니다.");
        // ※ 콘솔 로그에 delete 쿼리가 출력됩니다.
    }

    @Test
    @DisplayName("유저 매장 리스트 쿼리 확인")
    void testGetUserStoresQueryExecution() {
        // given: 사용자 생성
        User user = User.builder()
                .name("Test User")
                .email("testuser@example.com")
                .role(Role.GUEST)
                .build();
        user = userRepository.save(user);

        // 매장 생성 및 사용자와의 연결 (실제 서비스에서는 매장 가입 로직을 통해 연결)
        StoreCreateRequest createRequest = new StoreCreateRequest();
        createRequest.setStoreName("User Store");
        createRequest.setDescription("User Store Description");
        StoreCreateResponse createResponse = storeService.createStore(createRequest);
        storeService.joinUserStore(createResponse.getStoreCode(), user.getId());

        // when: 사용자의 매장 목록 조회
        UserStoresResponse userStoresResponse = storeService.getUserStores(user.getId());

        // then
        assertNotNull(userStoresResponse);
        assertFalse(userStoresResponse.getStores().isEmpty(), "사용자 매장 목록이 비어있으면 안됩니다.");
        // ※ 콘솔 로그를 통해 select 쿼리가 실행된 것을 확인할 수 있습니다.
    }

    @Test
    @DisplayName("매장 사용자 목록 조회 쿼리 확인")
    void testGetStoreUsersQueryExecution() {
        // given: 매장 생성
        StoreCreateRequest createRequest = new StoreCreateRequest();
        createRequest.setStoreName("Store For Users");
        createRequest.setDescription("Desc for store users");
        StoreCreateResponse createResponse = storeService.createStore(createRequest);
        Long storeId = createResponse.getStoreId();

        // 두 명의 사용자 생성 후 매장 가입
        User user1 = User.builder()
                .name("홍길동")
                .email("hong@example.com")
                .phoneNumber("010-1111-2222")
                .avatarUrl("http://avatar.com/1.png")
                .role(Role.WORKER)
                .build();
        User user2 = User.builder()
                .name("김철수")
                .email("kim@example.com")
                .phoneNumber("010-3333-4444")
                .avatarUrl("http://avatar.com/2.png")
                .role(Role.ADMINISTRATOR)
                .build();
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        storeService.joinUserStore(createResponse.getStoreCode(), user1.getId());
        storeService.joinUserStore(createResponse.getStoreCode(), user2.getId());

        // when: 매장 사용자 목록 조회
        StoreUsersResponse response = storeService.getStoreUsers(storeId);

        // then
        assertNotNull(response);
        assertEquals(2, response.getUsers().size(), "매장 사용자 목록에 두 명의 사용자가 포함되어야 합니다.");
        // ※ 콘솔 로그에 select 쿼리가 출력됨을 확인하세요.
    }

    @Test
    @DisplayName("매장 정보 조회 쿼리 확인 (스케줄 템플릿 없음)")
    void testGetStoreInfoNoScheduleTemplate() {
        // given: 사용자와 매장 생성 및 연결
        User user = User.builder()
                .name("Info User")
                .email("infouser@example.com")
                .role(Role.GUEST)
                .build();
        user = userRepository.save(user);

        StoreCreateRequest createRequest = new StoreCreateRequest();
        createRequest.setStoreName("Info Store");
        createRequest.setDescription("Store for info test");
        StoreCreateResponse createResponse = storeService.createStore(createRequest);
        Long storeId = createResponse.getStoreId();
        storeService.joinUserStore(createResponse.getStoreCode(), user.getId());

        // when: 매장 정보 조회 (스케줄 템플릿 관련 데이터가 없다면 빈 리스트 반환)
        var storeInfoResponse = storeService.getStoreInfo(storeId, user.getId());

        // then
        assertNotNull(storeInfoResponse);
        assertEquals(storeId, storeInfoResponse.getStoreId());
        assertTrue(storeInfoResponse.getScheduleTemplates().isEmpty(), "스케줄 템플릿이 없는 경우 빈 리스트여야 합니다.");
        // ※ 관련 select 쿼리들이 로그에 출력됩니다.
    }

    @Test
    @DisplayName("매장 입장 쿼리 확인 (중복 가입 예외)")
    void testJoinUserStoreAlreadyJoined() {
        // given: 사용자와 매장 생성
        User user = User.builder()
                .name("Join User")
                .email("joinuser@example.com")
                .role(Role.GUEST)
                .build();
        final User savedUser = userRepository.save(user);

        StoreCreateRequest createRequest = new StoreCreateRequest();
        createRequest.setStoreName("Join Store");
        createRequest.setDescription("Join store test");
        StoreCreateResponse createResponse = storeService.createStore(createRequest);

        // when: 첫 가입 시도
        storeService.joinUserStore(createResponse.getStoreCode(), savedUser.getId());

        // then: 동일 매장에 다시 가입 시도하면 예외가 발생해야 함
        assertThrows(StoreException.class, () ->
                storeService.joinUserStore(createResponse.getStoreCode(), savedUser.getId())
        );
        // ※ 콘솔 로그에 insert/select 쿼리가 실행된 것을 확인할 수 있습니다.
    }

    @Test
    @DisplayName("매장 정보 단순 조회 쿼리 확인")
    void testGetStoreSimpleInfo() {
        // given: 매장 생성
        StoreCreateRequest createRequest = new StoreCreateRequest();
        createRequest.setStoreName("Simple Info Store");
        createRequest.setDescription("Simple info description");
        StoreCreateResponse createResponse = storeService.createStore(createRequest);

        Store store = storeRepository.findById(createResponse.getStoreId()).orElseThrow();

        // when: storeCode로 단순 조회
        StoreResponse response = storeService.getStoreSimpleInfo(store.getStoreCode());

        // then
        assertNotNull(response, "응답이 null이 아니어야 합니다.");
        assertEquals(store.getId(), response.getStoreId());
        assertEquals(store.getStoreName(), response.getStoreName());
        assertEquals(store.getDescription(), response.getDescription());
        // ※ 콘솔 로그에 select 쿼리가 출력됨을 확인하세요.
    }
}
