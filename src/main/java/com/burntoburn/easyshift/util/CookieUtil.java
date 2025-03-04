package com.burntoburn.easyshift.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Optional;

public class CookieUtil {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    // 요청값 (이름, 값, 만료기간)을 바탕으로 HTTP 응답에 쿠키를 추가한다.
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    // 쿠키 이름을 입력받아 쿠키를 삭제
    // 실제로 삭제하는 방법은 없으므로 해당 쿠키를 빈값으로 바꾸고 만료시간을 0으로 설정해 만료 처리
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if( cookies == null) return;

        for(Cookie cookie : cookies) {
            if(name.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    // 객체를 직렬화해 쿠키의 값으로 들어갈 값으로 변환
    public static String serialize(Object object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    // 쿠키를 역질렬화 객체로 변환
    public static <T>  T deserialize(Cookie cookie, Class<T> tClass) {
        return tClass.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
