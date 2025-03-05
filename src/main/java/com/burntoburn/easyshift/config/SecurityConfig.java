package com.burntoburn.easyshift.config;

import com.burntoburn.easyshift.config.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private TokenProvider tokenProvider;

//    @Bean //h2 데이터 베이스에 대한 접근을 검증 하지않음
//    public WebSecurityCustomizer configure() {
//        return (web -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**")));
//    }
    
    @Bean
    public SecurityFilterChain defalutSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable) //form 로그인 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) //http Basic 비활성화
                .csrf(AbstractHttpConfigurer::disable) //csrf 보안 비활성화
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                //session 사용하지 않음
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.securityMatcher("/**")
                .authorizeHttpRequests((request) -> { //회원 가입과 가입시 유저 정보 입력을 제외하곤 모두 인증된 사용자만 허용
                    request.requestMatchers("/","/css/**","/images/**","/js/**","/favicon.ico","/h2-console/**").permitAll();
                    request.requestMatchers("/api/user/info","/api/user/login").permitAll();
                    request.anyRequest().permitAll();
                });

/*
        // 헤더를 확인할 커스텀 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.logout(logout -> logout.logoutSuccessUrl("/login"));

        // /api 로 시작하는 url인 경우 401상태 코드를 반환하도록 예외처리
        http.exceptionHandling(exceptionHandlingConfigurer -> {
            exceptionHandlingConfigurer.defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),new AntPathRequestMatcher("/api/**"));
        });
*/

        return http.build();
    }

/*    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }*/
}
