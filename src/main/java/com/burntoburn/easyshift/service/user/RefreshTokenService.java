package com.burntoburn.easyshift.service.user;

import com.burntoburn.easyshift.entity.user.RefreshToken;
import com.burntoburn.easyshift.repository.user.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken (String refreshtoken) {
        return refreshTokenRepository.findByRefreshToken(refreshtoken).orElseThrow(() -> new IllegalArgumentException("Unexpected Token"));
    }

}
