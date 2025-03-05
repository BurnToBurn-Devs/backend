package com.burntoburn.easyshift.dto.store.use;

import com.burntoburn.easyshift.entity.store.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreResponse {
    private Long storeId;
    private String storeName;
    private String description;

    public static StoreResponse fromEntity(Store store) {
        return new StoreResponse(store.getId(), store.getStoreName(), store.getDescription());
    }

}
