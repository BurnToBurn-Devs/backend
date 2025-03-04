package com.burntoburn.easyshift.oauth2.user;

import lombok.Getter;

import java.util.Map;

@Getter
public class KakaoOAuth2User {

        private final Map<String, Object> attributes;
        private final String id;
        private final String name;
        private final String email;
        private final String profileImageUrl;

        public KakaoOAuth2User( Map<String, Object> attributes) {
            // attributes 맵의 kakao_account 키의 값에 실제 attributes 맵이 할당되어 있음
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
            this.attributes = attributes;

            this.id = ((Long) attributes.get("id")).toString();
            this.email = (String) kakaoAccount.get("email");
            this.name = (String)  kakaoAccount.get("nickname");
            this.profileImageUrl = (String) kakaoProfile.get("profile_image_url");
        }

}
