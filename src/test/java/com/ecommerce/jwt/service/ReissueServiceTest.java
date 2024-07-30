package com.ecommerce.jwt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecommerce.exception.ExpiredTokenException;
import com.ecommerce.exception.InvalidTokenException;
import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.jwt.dto.TokenPair;
import com.ecommerce.jwt.entity.RefreshToken;
import com.ecommerce.jwt.repository.RefreshTokenRepository;
import com.ecommerce.member.entity.Role;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReissueServiceTest {

  @Mock
  JwtUtil jwtUtil;

  @Mock
  RefreshTokenRepository refreshTokenRepository;

  @InjectMocks
  ReissueService reissueService;

  @DisplayName("토큰 재발급에 성공한다.")
  @Test
  void reissueSuccess() {
    // given
    String validRefreshToken = "validRefreshToken";
    Long memberId = 1L;
    String email = "test@example.com";
    String role = Role.BASIC.name();
    String newAccessToken = "newAccessToken";
    String newRefreshToken = "newRefreshToken";

    RefreshToken refreshTokenEntity = mock(RefreshToken.class);

    when(jwtUtil.isExpired(validRefreshToken)).thenReturn(false);
    when(jwtUtil.isRefreshToken(validRefreshToken)).thenReturn(true);
    when(jwtUtil.getEmail(validRefreshToken)).thenReturn(email);
    when(jwtUtil.getMemberId(validRefreshToken)).thenReturn(memberId);
    when(jwtUtil.getRole(validRefreshToken)).thenReturn(Role.BASIC);
    when(refreshTokenRepository.findByEmailAndRefreshToken(email, validRefreshToken)).thenReturn(
        refreshTokenEntity);
    when(jwtUtil.createAccessToken(email, memberId, role)).thenReturn(newAccessToken);
    when(jwtUtil.createRefreshToken(email, memberId, role)).thenReturn(newRefreshToken);

    // when
    TokenPair tokenPair = reissueService.reissue(validRefreshToken);

    // then
    verify(refreshTokenEntity).update(newRefreshToken);
    assertEquals(newAccessToken, tokenPair.getAccessToken());
    assertEquals(newRefreshToken, tokenPair.getRefreshToken());
  }

  @DisplayName("만료된 Refresh token으로 재발급에 실패한다.")
  @Test
  void reissueFailureExpiredToken() {
    // given
    String expiredToken = "expiredToken";

    doThrow(new ExpiredJwtException(null, null, "Token expired"))
        .when(jwtUtil).isExpired(expiredToken);

    // when & then
    assertThrows(ExpiredTokenException.class, () -> reissueService.reissue(expiredToken));
  }

  @DisplayName("유효하지 않은 Refresh token으로 재발급에 실패한다.")
  @Test
  void reissueFailureInvalidToken() {
    // given
    String invalidRefreshToken = "invalidRefreshToken";

    when(jwtUtil.isExpired(invalidRefreshToken)).thenReturn(false);
    when(jwtUtil.isRefreshToken(invalidRefreshToken)).thenReturn(false);

    // whe & then
    assertThrows(InvalidTokenException.class, () -> reissueService.reissue(invalidRefreshToken));
  }

  @DisplayName("DB에 존재하지 않는 Refresh token으로 재발급에 실패한다.")
  @Test
  void reissueFailureTokenNotInDatabase() {
    String validRefreshToken = "validRefreshToken";
    String email = "test@example.com";

    when(jwtUtil.isExpired(validRefreshToken)).thenReturn(false);
    when(jwtUtil.isRefreshToken(validRefreshToken)).thenReturn(true);
    when(jwtUtil.getEmail(validRefreshToken)).thenReturn(email);
    when(refreshTokenRepository.findByEmailAndRefreshToken(email, validRefreshToken)).thenReturn(
        null);

    assertThrows(InvalidTokenException.class, () -> reissueService.reissue(validRefreshToken));
  }
}