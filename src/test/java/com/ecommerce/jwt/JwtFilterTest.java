package com.ecommerce.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

  @Mock
  private JwtUtil jwtUtil;
  @InjectMocks
  private JwtFilter jwtFilter;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  @Mock
  private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.getContext().setAuthentication(null);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @DisplayName("회원 인증에 성공한다.")
  @Test
  void doFilterInternalSuccess() throws ServletException, IOException {
    // given
    String email = "test@example.com";
    String accessToken = "access-token";
    request.addHeader("Access", accessToken);
    when(jwtUtil.isExpired(accessToken)).thenReturn(false);
    when(jwtUtil.isAccessToken(accessToken)).thenReturn(true);
    when(jwtUtil.getEmail(accessToken)).thenReturn(email);

    // when
    jwtFilter.doFilterInternal(request, response, filterChain);

    // then
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @DisplayName("Access Token이 없으면 아무 작업도 하지 않는다.")
  @Test
  void doFilterInternalNullAccessToken() throws ServletException, IOException {
    // when
    jwtFilter.doFilterInternal(request, response, filterChain);

    // then
    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @DisplayName("만료된 Access Token일 경우 인증에 실패한다.")
  @Test
  void doFilterInternalExpiredAccessToken() throws ServletException, IOException {
    // given
    String accessToke = "access-token";
    request.addHeader("Access", accessToke);
    doThrow(new ExpiredJwtException(null, null, null)).when(jwtUtil).isExpired(accessToke);

    // when
    jwtFilter.doFilterInternal(request, response, filterChain);

    // then
    assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @DisplayName("유효하지 않은 Access Token일 경우 인증에 실패한다.")
  @Test
  void doFilterInternalInvalidAccessToken() throws ServletException, IOException {
    // given
    String accessToken = "access-token";
    request.addHeader("Access", accessToken);
    when(jwtUtil.isAccessToken(accessToken)).thenReturn(false);

    // when
    jwtFilter.doFilterInternal(request, response, filterChain);

    // then
    assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
