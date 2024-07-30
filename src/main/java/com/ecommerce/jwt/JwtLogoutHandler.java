package com.ecommerce.jwt;

import com.ecommerce.jwt.repository.RefreshTokenRepository;
import com.ecommerce.utils.HeaderUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@RequiredArgsConstructor
@Slf4j
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {

    log.debug("logout 시작");
    String accessToken = HeaderUtil.getAccessToken(request);
    String email = jwtUtil.getEmail(accessToken);
    refreshTokenRepository.deleteByEmail(email);

  }
}
