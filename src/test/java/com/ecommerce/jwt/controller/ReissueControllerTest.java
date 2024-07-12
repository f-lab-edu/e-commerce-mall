package com.ecommerce.jwt.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.exception.ExpiredTokenException;
import com.ecommerce.exception.InvalidTokenException;
import com.ecommerce.jwt.dto.TokenPair;
import com.ecommerce.jwt.service.ReissueService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ReissueControllerTest {

  private MockMvc mockMvc;

  @Mock
  private ReissueService reissueService;

  @InjectMocks
  private ReissueController reissueController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(reissueController).build();
  }

  @DisplayName("토큰 재발급에 성공한다.")
  @Test
  void reissue() throws Exception {
    // given
    String refreshToken = "validToken";
    String newAccessToken = "newAccessToken";
    String newRefreshToken = "newRefreshToken";
    Cookie refreshCookie = new Cookie("Refresh", refreshToken);

    TokenPair tokenPair = new TokenPair(newAccessToken, newRefreshToken);

    when(reissueService.reissue(refreshToken)).thenReturn(tokenPair);

    // when & then
    mockMvc.perform(post("/reissue").cookie(refreshCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value(newAccessToken))
        .andExpect(jsonPath("$.refreshToken").value(newRefreshToken));
  }

  @DisplayName("Cookie에 토큰이 없어 400 코드를 반환한다.")
  @Test
  void reissueRefreshTokenNull() throws Exception {
    mockMvc.perform(post("/reissue").cookie(new Cookie("null", null)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("refresh token null"));
  }

  @DisplayName("만료된 토큰으로 401 코드를 반환한다.")
  @Test
  void reissueExpiredToken() throws Exception {
    // given
    String refreshToken = "expiredToken";
    Cookie refreshCookie = new Cookie("Refresh", refreshToken);

    when(reissueService.reissue(refreshToken)).thenThrow(
        new ExpiredTokenException("Refresh token expired"));

    // when
    mockMvc.perform(post("/reissue").cookie(refreshCookie))
        // then
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Refresh token expired"));
  }

  @DisplayName("유효하지 않은 토큰으로 403 코드를 반환한다.")
  @Test
  void reissueInvalidToken() throws Exception {
    String refreshToken = "invalidToken";
    Cookie refreshCookie = new Cookie("Refresh", refreshToken);

    when(reissueService.reissue(refreshToken)).thenThrow(
        new InvalidTokenException("Invalid refresh token"));

    mockMvc.perform(post("/reissue").cookie(refreshCookie))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Invalid refresh token"));
  }

  @DisplayName("시스템 상의 문제로 500 코드를 반환한다.")
  @Test
  void testReissue_InternalServerError() throws Exception {
    String refreshToken = "validToken";
    Cookie refreshCookie = new Cookie("Refresh", refreshToken);

    when(reissueService.reissue(refreshToken)).thenThrow(new RuntimeException("Unexpected error"));

    mockMvc.perform(post("/reissue").cookie(refreshCookie))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Unexpected error"));
  }

}