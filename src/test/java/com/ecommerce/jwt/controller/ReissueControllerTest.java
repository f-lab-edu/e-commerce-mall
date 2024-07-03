package com.ecommerce.jwt.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.jwt.entity.RefreshToken;
import com.ecommerce.jwt.repository.RefreshTokenRepository;
import com.ecommerce.jwt.utils.TestTokenUtil;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc // MockMvc 자동 설정
class ReissueControllerTest {

  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  private TestTokenUtil testTokenUtil;
  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  private String email = "test@example.com";
  private final String url = "/reissue";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    refreshTokenRepository.deleteAll();
  }

  @DisplayName("토큰 재발급에 성공한다.")
  @Test
  void reissueSuccess() throws Exception {
    // given
    // 리프레시 토큰 생성
    final String refreshToken = testTokenUtil.generateValidRefreshToken(email);

    // 생성한 리프레시토큰 DB에 저장
    RefreshToken refreshTokenEntity = RefreshToken.builder()
        .email(email)
        .refreshToken(refreshToken)
        .build();
    refreshTokenEntity.setExpiration();
    refreshTokenRepository.save(refreshTokenEntity);

    // when
    // 테스트 실행 시작!
    final ResultActions result = mockMvc.perform(
        post(url).cookie(new MockCookie("Refresh", refreshToken)));

    // then
    MvcResult mvcResult = result.andExpect(status().isOk()).andReturn();
    String bodyValue = mvcResult.getResponse().getContentAsString();
    String resultAccessToken = JsonPath.parse(bodyValue).read("accessToken");
    String resultRefreshToken = JsonPath.parse(bodyValue).read("refreshToken");

    RefreshToken resultRefreshTokenEntity = refreshTokenRepository.findByEmailAndRefreshToken(email,
        resultRefreshToken);
    Assertions.assertNotNull(resultRefreshTokenEntity); // 응답 값 Refresh Token으로 DB에 저장됐는지 확인

    result.andExpect(
            header().string("Access", resultAccessToken)) // 헤더와 응답 값 Access Token 값이 일치하는지 확인
        .andExpect(
            cookie().value("Refresh", resultRefreshToken)) // 쿠키와 응답 값 Refresh Token 값이 일치하는지 확인
        .andDo(print());
  }

  @DisplayName("만료된 Refresh Token으로 재발급 요청 시 401 상태 코드를 반환한다")
  @Test
  void reissueFailureExpiredToken() throws Exception {
    // given
    // 만료된 리프레시 토큰 생성
    final String expiredRefreshToken = testTokenUtil.generateExpiredRefreshToken(email);

    // when & then
    mockMvc.perform(post(url)
            .cookie(new MockCookie("Refresh", expiredRefreshToken)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Refresh token expired"))
        .andDo(print());
  }

  @DisplayName("유효하지 않은 Refresh token으로 재발급 요청 시 403 상태 코드를 반환한다")
  @Test
  void reissueFailureInvalidToken() throws Exception {
    // given
    final String invalidRefreshToken = testTokenUtil.generateInvalidRefreshToken();

    // when & then
    mockMvc.perform(post(url)
            .cookie(new MockCookie("Refresh", invalidRefreshToken)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Invalid refresh token"))
        .andDo(print());
  }

  @DisplayName("리프레시 토큰 없는 요청 시 400 상태 코드를 반환한다")
  @Test
  void testReissueNoToken() throws Exception {
    // when & then
    mockMvc.perform(post(url)
            .cookie(new MockCookie("NoRefreshToken", "Nothing")))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("refresh token null"));
  }
}