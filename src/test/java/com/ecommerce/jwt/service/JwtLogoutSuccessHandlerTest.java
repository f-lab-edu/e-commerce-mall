package com.ecommerce.jwt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

class JwtLogoutSuccessHandlerTest {

  private JwtLogoutSuccessHandler handler;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  void setUp() {
    handler = new JwtLogoutSuccessHandler();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @DisplayName("로그아웃 성공 후 200 상태 코드를 반환한다.")
  @Test
  void onLogoutSuccess() throws ServletException, IOException {

    // Given
    Authentication authentication = null; // can be mocked if necessary

    // When
    handler.onLogoutSuccess(request, response, authentication);

    // Then
    assertEquals(200, response.getStatus());
  }
}