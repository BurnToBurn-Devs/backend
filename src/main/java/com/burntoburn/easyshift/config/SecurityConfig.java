package com.burntoburn.easyshift.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests((request) -> { // 경로에 대한 엑세스 설정
            request.requestMatchers(antMatcher("/login")).permitAll();
            request.requestMatchers(antMatcher("/signup")).permitAll();
            request.requestMatchers(antMatcher("/user")).permitAll();

            request.anyRequest().authenticated();
        });

        //로그인 설정
        http.formLogin(login -> login //form 방식으로 로그인
                .loginPage("/login") //로그인 page 경로
                .defaultSuccessUrl("/articles",true) // 성공시 이동할 경로
                .permitAll()); // 권한 모두 허용

        //로그아웃 설정
        http.logout(logout -> logout //
                .logoutSuccessUrl("/login") //
                .invalidateHttpSession(true));

        return http.build();
    }

}
