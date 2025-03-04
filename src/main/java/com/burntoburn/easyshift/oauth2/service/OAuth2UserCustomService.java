package com.burntoburn.easyshift.oauth2.service;

import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.oauth2.user.KakaoOAuth2User;
import com.burntoburn.easyshift.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 요청을 바탕으로 유저 정보를 담은 객체 반환
        saveOrUpdate(oAuth2User);
        return oAuth2User;
    }

    // 유저가 있으면 업데이트, 없으면 유저 생성
    private void saveOrUpdate(OAuth2User oAuth2User) {
        KakaoOAuth2User kakaoOAuth2User = new KakaoOAuth2User(oAuth2User.getAttributes());
        String email = kakaoOAuth2User.getEmail();

        User user= userRepository.findByEmail(email)
                .map(entity -> entity.update(email))
                .orElse(User.builder()
                        .email(email)
                        .name(kakaoOAuth2User.getName())
                        .avatarUrl(kakaoOAuth2User.getProfileImageUrl())
                        .build());

        userRepository.save(user);
    }

}
