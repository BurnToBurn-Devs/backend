package com.burntoburn.easyshift.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            getTokenFromRequest(request)
                    .filter(tokenProvider::validToken)
                    .ifPresent(this::setAuthentication);
        } catch (Exception e) {
            log.error("Error while setting JWT authentication", e);
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HEADER_AUTHORIZATION))
                .filter(header -> header.startsWith(TOKEN_PREFIX))
                .map(header -> header.substring(TOKEN_PREFIX.length()));
    }

    private void setAuthentication(String token) {
        Authentication authentication = tokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Setting authentication to security context is done. '{}'", authentication.getName());
    }

}
