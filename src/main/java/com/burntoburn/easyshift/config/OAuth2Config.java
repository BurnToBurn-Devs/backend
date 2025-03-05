package com.burntoburn.easyshift.config;

import com.burntoburn.easyshift.config.jwt.TokenProvider;
import com.burntoburn.easyshift.oauth2.handler.OAuth2FailureHandler;
import com.burntoburn.easyshift.oauth2.handler.OAuth2SuccessHandler;
import com.burntoburn.easyshift.oauth2.service.OAuth2UserCustomService;
import com.burntoburn.easyshift.repository.user.RefreshTokenRepository;
import com.burntoburn.easyshift.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class OAuth2Config {
    private TokenProvider tokenProvider;
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public SecurityFilterChain oAuth2FilterChain(HttpSecurity http) throws Exception {


        http.securityMatcher("/oauth2/**", "/login/**") // OAuth2 관련 URL만 매칭
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> {
                    oauth2.loginPage("/login")
                                .userInfoEndpoint(
                                    userInfoEndpointConfig ->
                                            userInfoEndpointConfig.userService(
                                                    oAuth2UserCustomService));
                    // 인증 성공 시 실행할 핸들러
                    oauth2.successHandler(oAuth2SuccessHanlder());
                    oauth2.failureHandler(oAuth2FailureHandler());
                }
        );

        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHanlder() {
        return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository, userService);
    }

    @Bean
    public OAuth2FailureHandler oAuth2FailureHandler() {
        return new OAuth2FailureHandler();
    }
}
