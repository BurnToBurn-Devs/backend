package com.burntoburn.easyshift.dto.store.use;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreUpdateRequest {
    @NotEmpty(message = "매장 이름은 필수입니다.")
    private String storeName;
    @NotEmpty(message = "매장 설명은 필수입니다.")
    private String description;
}
