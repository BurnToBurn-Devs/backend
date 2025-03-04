package com.burntoburn.easyshift.dto.token;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccessTokenRequest {

    private String refreshToken;

}
