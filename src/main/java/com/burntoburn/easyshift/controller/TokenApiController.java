package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.dto.token.CreateAccessTokenRequest;
import com.burntoburn.easyshift.dto.token.CreateAccessTokenResponse;
import com.burntoburn.easyshift.service.user.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(("/login"))
@RequiredArgsConstructor
@Slf4j
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<CreateAccessTokenResponse> createAccessToken(@RequestBody CreateAccessTokenRequest request){
        String newToken = tokenService.createNewAccessToken(request.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateAccessTokenResponse(newToken));
    }

    @GetMapping("/oauth2/code/kakao")
    public String signUpKakao(@RequestParam String code) {
        log.info("code: {}", code);
        return "test";
    }
}
