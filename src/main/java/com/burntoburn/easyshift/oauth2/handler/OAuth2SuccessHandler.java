package com.burntoburn.easyshift.oauth2.handler;

import com.burntoburn.easyshift.config.jwt.TokenProvider;
import com.burntoburn.easyshift.entity.user.RefreshToken;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.oauth2.repository.OAuth2AuthorizationRequestRepository;
import com.burntoburn.easyshift.oauth2.user.KakaoOAuth2User;
import com.burntoburn.easyshift.repository.user.RefreshTokenRepository;
import com.burntoburn.easyshift.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.burntoburn.easyshift.util.CookieUtil;
import java.io.IOException;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;
    private final UserService userService;

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(2);
    // 프론트엔드 콜백 페이지 URL ( 추후 수정 예정 )
    public static final String FRONTEND_CALLBACK_URL = "http://localhost:3000/";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        log.info("OAuth2 Login 성공!");
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        // Oauth2를 통해 불러온 유저 정보
        KakaoOAuth2User kakaoOAuth2User = new KakaoOAuth2User(oAuth2User.getAttributes());
        User user = userService.findByEmail(kakaoOAuth2User.getEmail());

        // 자체 access token 생성
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        // 리프레시 토큰 생성
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveToken(user, refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        //헤더에 Access Token 추가
        response.setHeader("Authorization", "Bearer " + accessToken);

        // JSON 응답 생성
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse;
        if (user.getRole() != null) {
            // 기존 사용자 (회원가입 완료)
            jsonResponse = "{ " +
                    "\"success\": true, " +
                    "\"response\": { " +
                    "\"userId\": \"" + user.getId() + "\", " +
                    "\"email\": \"" + user.getEmail() + "\", " +
                    "\"phoneNumber\": \"" + user.getPhoneNumber() + "\", " +
                    "\"role\": \"" + user.getRole() + "\", " +
                    "\"avatarUrl\": \"" + user.getAvatarUrl() + "\", " +
                    "\"needsSignup\": false }, " +
                    "\"error\": null " +
                    "}";
        } else {
            // 신규 사용자 (추가 회원가입 필요)
            jsonResponse = "{ " +
                    "\"success\": true, " +
                    "\"response\": { " +
                    "\"userId\": \"" + user.getId() + "\", " +
                    "\"email\": \"" + user.getEmail() + "\", " +
                    "\"phoneNumber\": null, " +
                    "\"role\": null, " +
                    "\"avatarUrl\": \"" + user.getAvatarUrl() + "\", " +
                    "\"needsSignup\": true }, " +
                    "\"error\": null " +
                    "}";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken); // 헤더에 토큰 추가

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonResponse, headers);

        RestTemplate restTemplate = new RestTemplate();
        // 프론트엔드의 post 방식을 json 데이터 전송.. (프론트엔드 엔드 포인트 필요)
        restTemplate.postForEntity(FRONTEND_CALLBACK_URL, requestEntity, String.class);

        // JSON 응답 전송
        response.getWriter().write(jsonResponse);
        // 인증 설정 값 삭제
        clearAuthenticationAttributes(request, response);
    }

    // 생성된 리프레시 토큰을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    // RefreshToken 이 DB에 있는지 확인하고 업데이트 하거나 저장
    private void saveToken(User user, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .map(entity -> entity.update(newRefreshToken))
                .orElse(RefreshToken.builder().refreshToken(newRefreshToken).user(user).build());
        refreshTokenRepository.saveAndFlush(refreshToken);
    }

    // 인증 관련 설정값을 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        oAuth2AuthorizationRequestRepository.removeAuthorizationRequest(request, response);
    }


}
