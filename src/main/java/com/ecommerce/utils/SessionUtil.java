package com.ecommerce.utils;

import jakarta.servlet.http.HttpSession;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SessionUtil {

  private static final String LOGIN_MEMBER_ID = "LOGIN_MEMBER_ID";

  // 로그인한 회원 ID 세션에 저장
  public static void setMemberId(HttpSession session, Long id) {
    session.setAttribute(LOGIN_MEMBER_ID, id);
  }

  // 로그인한 회원 조회
  public static Object getMemberId(HttpSession session) {
    return session.getAttribute(LOGIN_MEMBER_ID);
  }

  // 로그아웃한 회원 ID 세션에서 제거
  public static void logoutMemberId(HttpSession session) {
    session.removeAttribute(LOGIN_MEMBER_ID);
  }
}
