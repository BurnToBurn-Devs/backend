package com.burntoburn.easyshift.dto.store;

import com.burntoburn.easyshift.dto.user.UserDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StoreUserDTO {
    private Long storeId;
    private String storeName;
    private List<UserDTO> users;
}
