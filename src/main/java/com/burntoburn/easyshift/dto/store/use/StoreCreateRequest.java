package com.burntoburn.easyshift.dto.store.use;

import com.burntoburn.easyshift.entity.store.Store;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StoreCreateRequest {
    @NotEmpty(message = "매장 이름은 필수입니다.")
    private String storeName;
    @NotEmpty(message = "매장 설명은 필수입니다.")
    private String description;


    public static Store toEntity(String storeName, String description) {
        return Store.builder()
                .storeName(storeName)
                .storeCode(UUID.randomUUID())
                .description(description)
                .build();
    }
}
