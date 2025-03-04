package com.burntoburn.easyshift.dto.schedule.res;

import com.burntoburn.easyshift.entity.store.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreInfo {
    private Long storeId;
    private String storeName;

    public static StoreInfo fromEntity(Store store){
        return new StoreInfo(store.getId(), store.getStoreName());
    }
}
