package com.burntoburn.easyshift.dto.store.use;

import com.burntoburn.easyshift.dto.user.UserDTO;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreUsersResponse {
    private UUID storeCode;
    private Long storeId;
    private String storeName;
    private String description;
    private List<UserDTO> users;

    public static StoreUsersResponse fromEntity(Store store, List<User> users) {
        List<UserDTO> userDTOs = users.stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .avatarUrl(user.getAvatarUrl())
                        .role(user.getRole())
                        .build())
                .collect(Collectors.toList());

        return new StoreUsersResponse(
                store.getStoreCode(),
                store.getId(),
                store.getStoreName(),
                store.getDescription(),
                userDTOs
        );
    }
}
