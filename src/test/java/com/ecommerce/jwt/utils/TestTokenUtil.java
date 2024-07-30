package com.ecommerce.jwt.utils;

import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.member.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestTokenUtil {

  private final JwtUtil jwtUtil;

  @Autowired
  public TestTokenUtil(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  public String generateValidRefreshToken(String email, String role) {
    return jwtUtil.createRefreshToken(email, role);
  }

  public String generateExpiredRefreshToken(String email, String role) {
    return jwtUtil.createJWTForTest("refresh", email, role, -60000L);
  }

  public String generateInvalidRefreshToken() {
    return jwtUtil.createJWTForTest("invalid", "invalid", Role.BASIC.name(), 60000L);
  }
}
