package com.ecommerce.utils;

import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberUtil {

  private final JwtUtil jwtUtil;
  private final MemberService memberService;

  public Member getLoginMember(HttpServletRequest request) {
    String accessToken = request.getHeader("Access");
    String email = jwtUtil.getEmail(accessToken);

    return memberService.findByEmail(email);
  }
}
