package com.ecommerce.utils;

import jakarta.servlet.http.HttpSession;

public class SessionUtil {
    private static final String LOGIN_MEMBERS_ID = "LOGIN_MEMBERS_ID";

    //인스턴스화 방지
    private SessionUtil() {
    }

    // 로그인한 회원 ID 세션에 저장
    public static void setMemberId(HttpSession session, Long id) {
        session.setAttribute(LOGIN_MEMBERS_ID, id);
    }

    // 로그아웃한 회원 ID 세션에서 제거
    public static void logoutMemberId(HttpSession session) {
        session.removeAttribute(LOGIN_MEMBERS_ID);
    }
}
