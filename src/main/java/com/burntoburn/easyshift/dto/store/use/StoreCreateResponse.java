package com.burntoburn.easyshift.dto.store.use;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreCreateResponse {
    private Long storeId;
    private String storeName;
    private UUID storeCode;

    public static StoreCreateResponse fromEntity(Long storeId, String storeName, UUID storeCode){
        return new StoreCreateResponse(storeId, storeName, storeCode);
    }
}
