package com.ecommerce.jwt.service;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ecommerce.exception.ExpiredTokenException;
import com.ecommerce.exception.InvalidTokenException;
import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.jwt.dto.TokenPair;
import com.ecommerce.jwt.entity.RefreshToken;
import com.ecommerce.jwt.repository.RefreshTokenRepository;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReissueServiceTest {

  @Autowired
  JwtUtil jwtUtil;

  @Autowired
  RefreshTokenRepository refreshTokenRepository;

  @Autowired
  ReissueService reissueService;

  private final String email = "test@example.com";
  private String refreshToken;

  @BeforeEach
  void setUp() {
    refreshTokenRepository.deleteAll();
    refreshToken = jwtUtil.createRefreshToken(email);
    RefreshToken refreshTokenEntity = RefreshToken.builder()
        .email(email)
        .refreshToken(refreshToken)
        .build();
    refreshTokenEntity.setExpiration();
    refreshTokenRepository.save(refreshTokenEntity);
  }

  @DisplayName("토큰 재발급에 성공한다.")
  @Test
  void reissueSuccess() throws InterruptedException {
    // given
    Thread.sleep(1000); // 짧은 대기시간 추가

    // when
    TokenPair tokenPair = reissueService.reissue(refreshToken);

    // then
    assertNotNull(tokenPair);
    assertNotNull(tokenPair.getAccessToken());
    assertNotNull(tokenPair.getRefreshToken());
    assertNotEquals(tokenPair.getAccessToken(), refreshToken);
    assertNotEquals(tokenPair.getRefreshToken(), refreshToken);

    RefreshToken savedToken = refreshTokenRepository.findByEmailAndRefreshToken(email,
        tokenPair.getRefreshToken());
    assertNotEquals(savedToken.getRefreshToken(), refreshToken);
  }

  @DisplayName("만료된 Refresh token으로 재발급에 실패한다.")
  @Test
  void reissueFailureExpiredToken() {
    // given
    String expiredToken = jwtUtil.createJWTForTest("refresh", email, -60000L);

    // when & then
    assertThrows(ExpiredTokenException.class, () -> reissueService.reissue(expiredToken));
  }

  @DisplayName("유효하지 않은 Refresh token으로 재발급에 실패한다.")
  @Test
  void reissueFailureInvalidToken() {
    // given
    String invalidRefreshToken = "invalidToken";

    // when & then
    assertThrows(MalformedJwtException.class, () -> reissueService.reissue(invalidRefreshToken));
  }

  @DisplayName("DB에 존재하지 않는 Refresh token으로 재발급에 실패한다.")
  @Test
  void reissueFailureNullToken() {
    // given
    String otherRefreshToken = jwtUtil.createJWTForTest("refresh", email, 60000L);

    // when
    assertThrows(InvalidTokenException.class, () -> reissueService.reissue(otherRefreshToken));
  }
}