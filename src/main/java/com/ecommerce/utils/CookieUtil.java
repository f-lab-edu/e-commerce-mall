package com.ecommerce.utils;

import jakarta.servlet.http.Cookie;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CookieUtil {
    public static Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 쿠키 24시간 유지
        cookie.setHttpOnly(true); // xss 공격 방어

        return cookie;
    }
}
