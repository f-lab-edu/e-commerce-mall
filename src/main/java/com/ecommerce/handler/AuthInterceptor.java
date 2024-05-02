package com.ecommerce.handler;

import com.ecommerce.utils.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    HttpSession session = request.getSession();

    Object loginMembersId = SessionUtil.getMemberId(session);

    if (loginMembersId == null) {
      throw new IllegalArgumentException("로그인이 필요합니다.");
    }
    return true;
  }
}
