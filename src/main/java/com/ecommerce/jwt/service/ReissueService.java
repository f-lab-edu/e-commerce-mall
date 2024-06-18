package com.ecommerce.jwt.service;

import com.ecommerce.exception.ExpiredTokenException;
import com.ecommerce.exception.InvalidTokenException;
import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.jwt.dto.TokenPair;
import com.ecommerce.jwt.entity.RefreshToken;
import com.ecommerce.jwt.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReissueService {

  private final JwtUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public TokenPair reissue(String refreshToken) {
    // expired check
    try {
      jwtUtil.isExpired(refreshToken);
    } catch (ExpiredJwtException e) {
      throw new ExpiredTokenException("Refresh token expired");
    }

    // Check if token is a valid refresh token
    if (!jwtUtil.isRefreshToken(refreshToken)) {
      throw new InvalidTokenException("Invalid refresh token");
    }

    String email = jwtUtil.getEmail(refreshToken);

    // Check if refresh token exists in DB
    RefreshToken refreshTokenEntity = refreshTokenRepository.findByEmailAndRefreshToken(email,
        refreshToken);
    if (refreshTokenEntity == null) {
      throw new InvalidTokenException("Invalid refresh token");
    }

    // Create new tokens
    String newAccess = jwtUtil.createAccessToken(email);
    String newRefresh = jwtUtil.createRefreshToken(email);

    refreshTokenEntity.update(newRefresh);

    return new TokenPair(newAccess, newRefresh);
  }
}
