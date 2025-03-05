package com.burntoburn.easyshift.service.store;

import com.burntoburn.easyshift.dto.store.use.*;

import java.util.UUID;

public interface StoreService {
    StoreCreateResponse createStore(StoreCreateRequest request);

    void updateStore(Long storeId, StoreUpdateRequest request);

    void deleteStore(Long storeId);

    UserStoresResponse getUserStores(Long userId);

    StoreUsersResponse getStoreUsers(Long storeId);

    StoreResponse getStoreSimpleInfo(UUID storeCode);

    StoreInfoResponse getStoreInfo(Long storeId, Long userId);

    void joinUserStore(UUID storeCode, Long userId);
}
