package com.burntoburn.easyshift.dto.store.use;

import com.burntoburn.easyshift.entity.store.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserStoresResponse {
    private List<StoreResponse> stores;

    public static UserStoresResponse fromEntity(List<Store> stores) {
        List<StoreResponse> storeResponses = stores.stream()
                .map(store -> new StoreResponse(
                        store.getId(),
                        store.getStoreName(),
                        store.getDescription()))
                .collect(Collectors.toList());
        return new UserStoresResponse(storeResponses);
    }
}
