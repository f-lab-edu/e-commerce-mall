package com.ecommerce.jwt.utils;

import com.ecommerce.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestTokenUtil {

  private final JwtUtil jwtUtil;

  @Autowired
  public TestTokenUtil(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  public String generateValidRefreshToken(String email) {
    return jwtUtil.createRefreshToken(email);
  }

  public String generateExpiredRefreshToken(String email) {
    return jwtUtil.createJWTForTest("refresh", email, -60000L);
  }

  public String generateInvalidRefreshToken() {
    return jwtUtil.createJWTForTest("invalid", "invalid", 60000L);
  }
}
