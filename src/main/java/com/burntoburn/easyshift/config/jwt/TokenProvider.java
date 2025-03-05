package com.burntoburn.easyshift.config.jwt;

import com.burntoburn.easyshift.entity.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
    
    private final JwtProperties jwtProperties;
    private Key signingKey;
    
    @PostConstruct
    protected void init() {
        String secret = jwtProperties.getSecretKey();
        if (secret == null || secret.isBlank()) {
            log.error("JWT secret key is missing. Please add it in application-oauth.yml");
            throw new IllegalStateException("Missing JWT secret key");
        }
        try {
            this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        } catch (WeakKeyException e) {
            int keyBits = secret.getBytes(StandardCharsets.UTF_8).length * 8;
            log.error("The provided JWT secret key is too weak ({} bits). Please update your application-oauth.yml " +
                    "with a secret_key that is at least 256 bits.", keyBits);
            throw e;
        }
    }
    
    public String generateToken(User user, Duration expiresIn) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiresIn.toMillis());
        return makeToken(expiryDate, user);
    }
    
    private String makeToken(Date expiry, User user) {
        Date now = new Date();
        
        return Jwts.builder()
                .header()
                .empty()
                .add("typ", "JWT")
                .and()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiry)
                .subject(user.getEmail())
                .claim("id", user.getId())
                .signWith(signingKey)
                .compact();
    }
    
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities =
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities),
                token,
                authorities
        );
    }
    
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) signingKey)
                .build().parseSignedClaims(token).getPayload();
    }

    // 토큰에서 사용자 ID를 추출하는 public 메서드
    public Long getUserIdFromToken(String token) {
        return getClaims(token).get("id", Long.class);
    }
}