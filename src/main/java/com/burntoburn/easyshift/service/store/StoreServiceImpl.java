package com.burntoburn.easyshift.service.store;

import com.burntoburn.easyshift.common.util.DateUtil;
import com.burntoburn.easyshift.dto.store.use.*;
import com.burntoburn.easyshift.dto.user.UserDTO;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.store.UserStore;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.exception.store.StoreException;
import com.burntoburn.easyshift.exception.user.UserException;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.store.UserStoreRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreServiceImpl implements StoreService {

    private final UserStoreRepository userStoreRepository;
    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final ShiftRepository shiftRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public StoreCreateResponse createStore(StoreCreateRequest request) {
        Store store = StoreCreateRequest.toEntity(request.getStoreName(), request.getDescription());

        Store savedStore = storeRepository.save(store);
        return new StoreCreateResponse(savedStore.getId(), savedStore.getStoreName(), savedStore.getStoreCode());
    }

    @Override
    @Transactional
    public void updateStore(Long storeId, StoreUpdateRequest request) {
        Store store = storeRepository.findById(storeId).orElseThrow(StoreException::storeNotFound);

        store.setStoreName(request.getStoreName());
        store.setDescription(request.getDescription());
    }

    @Override
    @Transactional
    public void deleteStore(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(StoreException::storeNotFound);

        storeRepository.delete(store);
    }

    @Override
    public UserStoresResponse getUserStores(Long userId) {
        List<Store> userStores = userStoreRepository.findStoresByUserId(userId);
        return UserStoresResponse.fromEntity(userStores);
    }

    @Override
    public StoreUsersResponse getStoreUsers(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(StoreException::storeNotFound);
        List<User> users = userStoreRepository.findUsersByStoreId(store.getId());
        return StoreUsersResponse.fromEntity(store, users);
    }

    @Override
    public StoreResponse getStoreSimpleInfo(UUID storeCode) {
        Store store = storeRepository.findByStoreCode(storeCode).orElseThrow(StoreException::storeNotFound);
        return StoreResponse.fromEntity(store);
    }

    @Override
    public StoreInfoResponse getStoreInfo(Long storeId, Long userId) {
        // 1. 사용자 매장 접근 권한 확인
        boolean isAuthorizedForStore = userStoreRepository.existsByUserIdAndStoreId(userId, storeId);
        if (!isAuthorizedForStore) {
            throw StoreException.storeAccessDenied();
        }

        // 2. 매장의 스케줄 템플릿 조회(없는 경우 빈 응답 반환)
        List<ScheduleTemplate> scheduleTemplates = scheduleTemplateRepository.findAllWithShiftTemplatesByStoreId(storeId);
        if (scheduleTemplates.isEmpty()) {
            return new StoreInfoResponse(storeId, Collections.emptyList(), null);
        }

        // 3. 가장 처음의 템플릿을 선택
        ScheduleTemplate selectedTemplate = scheduleTemplates.getFirst();

        // 4. 조회 기간 설정 (이번 주)
        LocalDate today = LocalDate.now();
        LocalDate startDate = DateUtil.getStartOfWeek(today);
        LocalDate endDate = DateUtil.getEndOfWeek(startDate);

        // 5. 선택된 템플릿에 해당하는 Shift 조회
        List<Shift> shifts = shiftRepository.findAllByScheduleTemplateIdAndDateBetween(
                storeId, selectedTemplate.getId(), startDate, endDate
        );

        return StoreInfoResponse.fromEntity(storeId, scheduleTemplates, selectedTemplate, shifts);
    }

    @Transactional
    @Override
    public void joinUserStore(UUID storeCode, Long userId) {
        Store store = storeRepository.findByStoreCode(storeCode).orElseThrow(StoreException::storeNotFound);

        User user = userRepository.findById(userId).orElseThrow(UserException::userNotFound);

        boolean alreadyJoined = userStoreRepository.existsByUserIdAndStoreId(userId, store.getId());
        if(alreadyJoined){
            throw StoreException.userAlreadyJoined();
        }

        UserStore userStore = UserStore.builder()
                .user(user)
                .store(store)
                .build();

        userStoreRepository.save(userStore);
    }
}
