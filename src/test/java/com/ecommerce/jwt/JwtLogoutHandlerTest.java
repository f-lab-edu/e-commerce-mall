package com.ecommerce.jwt;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecommerce.jwt.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class JwtLogoutHandlerTest {

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @InjectMocks
  private JwtLogoutHandler jwtLogoutHandler;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private Authentication authentication;

  @BeforeEach
  void setUp() {
    jwtLogoutHandler = new JwtLogoutHandler(jwtUtil, refreshTokenRepository);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    authentication = mock(Authentication.class);
  }

  @DisplayName("로그아웃에 성공한다.")
  @Test
  void logoutSuccess() {
    // given
    String email = "test@example.com";
    String accessToken = "access-token";

    request.addHeader("Access", accessToken);
    when(jwtUtil.getEmail(accessToken)).thenReturn(email);

    // when
    jwtLogoutHandler.logout(request, response, authentication);

    // then
    verify(jwtUtil, times(1)).getEmail(accessToken);
    verify(refreshTokenRepository, times(1)).deleteByEmail(email);
  }

  @DisplayName("Access Token이 없으면 아무 작업도 하지 않는다.")
  @Test
  void logoutNoAccessToken() {
    // when
    jwtLogoutHandler.logout(request, response, authentication);

    // then
    verify(refreshTokenRepository, never()).deleteByRefreshToken(anyString());
  }

  @DisplayName("유효하지 않은 Access Token일 경우 아무 작업도 하지 않는다.")
  @Test
  void logoutInvalidAccessToken() {
    // given
    String accessToken = "invalidAccessToken";
    request.addHeader("Access", accessToken);

    // when
    jwtLogoutHandler.logout(request, response, mock(Authentication.class));

    // then
    verify(refreshTokenRepository, never()).deleteByEmail(anyString());

  }

  @DisplayName("Refresh Token 삭제 시 예외가 발생하여 실패한다.")
  @Test
  void logoutDeleteRefreshTokenError() {
    // given
    String email = "tet@example.com";
    String accessToken = "access-token";
    request.addHeader("Access", accessToken);
    doThrow(new RuntimeException("Failed to delete refresh token")).when(refreshTokenRepository)
        .deleteByEmail(email);

    // when & then
    Assertions.assertThrows(RuntimeException.class, () ->
        jwtLogoutHandler.logout(request, response, authentication));
  }
}