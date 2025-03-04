package com.burntoburn.easyshift.config.jwt;

import com.burntoburn.easyshift.entity.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
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
        log.info("TokenProvider initializing...");
        try {
            validateSecretKey();
            initializeSigningKey();
            log.info("TokenProvider initialized successfully.");
        } catch (Exception e) {
            log.error("TokenProvider failed to initialize. {}", e.getMessage());
            throw e;
        }
    }

    private void validateSecretKey() {
        String secret = jwtProperties.getSecretKey();
        if (secret == null || secret.isBlank()) {
            log.error("JWT secret key is missing. Please add it in application-oauth.yml");
            throw new IllegalStateException("Missing JWT secret key");
        }

        if (secret.length() < 32) {
            log.warn("JWT secret key is too short (minimum 32 characters recommended). Current length: {}", secret.length());
        }
    }

    private void initializeSigningKey() {
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user, Duration expiresIn) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiresIn.toMillis());

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .subject(user.getEmail())
                .claim("id", user.getId())
                .signWith(signingKey)
                .compact();
    }

    public boolean validToken(String token) {
        if (token == null) {
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith((SecretKey) signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.debug("Not supported JWT token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.debug("Wrong JWT token format: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Failed to validate JWT token: {}", e.getMessage());
        }

        return false;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities),
                token,
                authorities
        );
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token) {
        return getClaims(token).get("id", Long.class);
    }

}