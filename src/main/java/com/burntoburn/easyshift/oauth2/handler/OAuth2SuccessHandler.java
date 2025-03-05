package com.burntoburn.easyshift.oauth2.handler;

import com.burntoburn.easyshift.config.jwt.TokenProvider;
import com.burntoburn.easyshift.entity.user.RefreshToken;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.user.RefreshTokenRepository;
import com.burntoburn.easyshift.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(2);
    // 프론트엔드 콜백 페이지 URL ( 추후 수정 예정 )
    public static final String FRONTEND_CALLBACK_URL = "http://localhost:3000/auth/callback";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        log.info("OAuth2 Login 성공!");
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();

        // Oauth2를 통해 불러온 유저 정보
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String,Object> kakaoAccount = (Map<String,Object>) attributes.get("kakao_account");
        Map<String,Object> profile = (Map<String,Object>) kakaoAccount.get("profile");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String phone = (String) attributes.get("phone_number");
        String avatarUrl = (String) profile.get("profile_image_url");

        String message;
        User user = userService.findByEmail(email);
        if (user != null) {
            message = "로그인 성공";
        } else {
            // 신규 사용자는 추가 가입 페이지로 리다이렉트해서 역할 선택 (리다이렉트 URL은 추후 수정)
            String signupUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/signup")
                    .queryParam("email", email)
                    .queryParam("name", name)
                    .queryParam("PhoneNumber", phone)
                    .queryParam("avatarUrl", avatarUrl)
                    .build()
                    .toUriString();
            getRedirectStrategy().sendRedirect(request, response, signupUrl);
            return;
        }

        // 자체 access token 생성
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        // 리프레시 토큰 생성 (OAuth 액세스 토큰은 포함하지 않음)
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveToken(user.getId(), refreshToken);

        // 로그인 성공 메시지와 access token 을 반환
        String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_CALLBACK_URL)
                .queryParam("token", accessToken)
                .queryParam("msg", message)
                .build()
                .toUriString();

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // RefreshToken 이 DB에 있는지 확인하고 업데이트 하거나 저장
    private void saveToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(RefreshToken.builder().refreshToken(newRefreshToken).id(userId).build());
        refreshTokenRepository.save(refreshToken);
    }

    // 인증 관련 설정값을 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
    }


}
