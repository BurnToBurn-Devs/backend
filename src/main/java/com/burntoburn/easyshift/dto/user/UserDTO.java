package com.burntoburn.easyshift.dto.user;

import com.burntoburn.easyshift.entity.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private Role role;
}
