package com.ecommerce.utils;

import jakarta.servlet.http.Cookie;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CookieUtil {

  private static final String REFRESH_TOKEN_KEY = "Refresh";
  private static final int REFRESH_TOKEN_AGE = 24 * 60 * 60;

  public static String getRefreshToken(Cookie[] cookies) {
    String value = null;
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(REFRESH_TOKEN_KEY)) {
        value = cookie.getValue();
      }
    }
    return value;
  }

  public static Cookie createRefreshCookie(String refreshToken) {
    return createCookie(REFRESH_TOKEN_KEY, refreshToken, REFRESH_TOKEN_AGE);
  }

  public static Cookie deleteRefreshCookie() {
    return createCookie(REFRESH_TOKEN_KEY, null, 0);
  }

  private Cookie createCookie(String key, String value, int age) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(age); // 쿠키 24시간 유지
    cookie.setHttpOnly(true); // xss 공격 방어

    return cookie;
  }
}
