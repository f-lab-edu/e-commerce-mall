package com.ecommerce.jwt;

import com.ecommerce.jwt.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    System.out.println("logout 시작");
    String accessToken = request.getHeader("Access");
    String email = jwtUtil.getEmail(accessToken);
    refreshTokenRepository.deleteByEmail(email);

  }
}
