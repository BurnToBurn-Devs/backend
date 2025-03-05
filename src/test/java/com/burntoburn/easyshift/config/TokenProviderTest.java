package com.burntoburn.easyshift.config;

import com.burntoburn.easyshift.config.jwt.JwtProperties;
import com.burntoburn.easyshift.config.jwt.TokenProvider;
import com.burntoburn.easyshift.entity.user.Role;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.user.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TokenProviderTest {
    
    @Autowired
    private TokenProvider tokenProvider;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtProperties jwtProperties;
    
    @DisplayName("generateToken() : 유저 정보와 만료 기간으로 토큰을 생성한다.")
    @Test
    void generateToken() {
        // given
        User testUser = userRepository.save(
                User.builder()
                        .email("user@gmail.com")
                        .name("홍길동")
                        .role(Role.WORKER)
                        .build()
        );
        
        // when
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));
        
        // then: parse using the modern parserBuilder() API
        Long userId = Jwts.parser()
                .verifyWith(
                        Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8))
                )
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", Long.class);
        
        assertThat(userId).isEqualTo(testUser.getId());
    }
    
    @DisplayName("validToken() : 만료된 토큰이면 false를 반환한다.")
    @Test
    void validToken_invalidToken() {
        // given
        String token = JwtFactory.builder()
                .expiration(new java.util.Date(System.currentTimeMillis() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);
        
        // when
        boolean isValid = tokenProvider.validToken(token);
        
        // then
        assertThat(isValid).isFalse();
    }
    
    @DisplayName("validToken() : 만료되지 않은 토큰이면 true를 반환한다.")
    @Test
    void validToken_validToken() {
        // given
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);
        
        // when
        boolean result = tokenProvider.validToken(token);
        
        // then
        assertThat(result).isTrue();
    }
    
    @DisplayName("getAuthentication() : 토큰에서 인증 정보를 가져온다.")
    @Test
    void getAuthentication() {
        // given
        String userEmail = "user@gmail.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);
        
        // when
        Authentication authentication = tokenProvider.getAuthentication(token);
        
        // then
        org.springframework.security.core.userdetails.User userDetails =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        assertThat(userDetails.getUsername()).isEqualTo(userEmail);
    }
    
}