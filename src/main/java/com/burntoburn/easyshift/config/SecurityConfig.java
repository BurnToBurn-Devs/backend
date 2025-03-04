package com.burntoburn.easyshift.config;

import com.burntoburn.easyshift.config.jwt.TokenAuthenticationFilter;
import com.burntoburn.easyshift.config.jwt.TokenProvider;
import com.burntoburn.easyshift.oauth2.handler.OAuth2FailureHandler;
import com.burntoburn.easyshift.oauth2.handler.OAuth2SuccessHandler;
import com.burntoburn.easyshift.oauth2.repository.OAuth2AuthorizationRequestRepository;
import com.burntoburn.easyshift.oauth2.service.OAuth2UserCustomService;
import com.burntoburn.easyshift.repository.user.RefreshTokenRepository;
import com.burntoburn.easyshift.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Objects;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

    private static final String[] PUBLIC_STATIC_RESOURCES = {
            "/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**"
    };
    private static final String[] PUBLIC_API_ENDPOINTS = {
            "/api/user/info", "/api/user/login", "/api/public/**", "/api/auth/**", "/oauth2/**", "/login/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        validateDependencies();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // Authorization rules
                .authorizeHttpRequests(this::configureAuthorization)
                // OAuth2 login configuration
                .oauth2Login(this::configureOAuth2Login)
                // JWT token filter
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                // Logout configuration
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true))
                // Exception handling
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(org.springframework.http.HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")
                        ));

        return http.build();
    }

    private void configureAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize
                .requestMatchers(PUBLIC_STATIC_RESOURCES).permitAll()
                .requestMatchers(PUBLIC_API_ENDPOINTS).permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll();
    }

    // OAuth2 login configuration
    private void configureOAuth2Login(OAuth2LoginConfigurer<HttpSecurity> oauth2) {
        oauth2
                .authorizationEndpoint(authEndPoint ->
                        authEndPoint.authorizationRequestRepository(oAuth2AuthorizationRequestRepository))
                .userInfoEndpoint(userInfoEndpoint ->
                        userInfoEndpoint.userService(oAuth2UserCustomService))
                .successHandler(oAuth2SuccessHandler())
                .failureHandler(oAuth2FailureHandler());
    }

    private void validateDependencies() {
        Objects.requireNonNull(tokenProvider, "TokenProvider must not be null");
        Objects.requireNonNull(oAuth2UserCustomService, "OAuth2UserCustomService must not be null");
        Objects.requireNonNull(refreshTokenRepository, "RefreshTokenRepository must not be null");
        Objects.requireNonNull(userService, "UserService must not be null");
        Objects.requireNonNull(oAuth2AuthorizationRequestRepository,
                "OAuth2AuthorizationRequestRepository must not be null");

        log.debug("Security configuration dependencies validated successfully");
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(
                tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestRepository,
                userService);
    }

    @Bean
    public OAuth2FailureHandler oAuth2FailureHandler() {
        return new OAuth2FailureHandler();
    }
}