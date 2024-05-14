package com.ecommerce.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CookieUtil {

    public static String getCookieValue(HttpServletRequest request, String key) {
        String value = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(key)) {
                value = cookie.getValue();
            }
        }
        return value;
    }
    public static Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 쿠키 24시간 유지
        cookie.setHttpOnly(true); // xss 공격 방어

        return cookie;
    }
}
