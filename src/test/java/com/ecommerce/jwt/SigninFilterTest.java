package com.ecommerce.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecommerce.jwt.entity.RefreshToken;
import com.ecommerce.jwt.repository.RefreshTokenRepository;
import com.ecommerce.member.dto.SigninRequest;
import com.ecommerce.member.entity.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import javax.naming.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class) // Mockito 초기화
class SigninFilterTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @InjectMocks
  private SigninFilter signinFilter;

  private ObjectMapper objectMapper; // 직렬화, 역직렬화에 사용

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    signinFilter = new SigninFilter(authenticationManager, jwtUtil, refreshTokenRepository);


  }

  @DisplayName("로그인시 회원 인증에 성공한다.")
  @Test
  void attemptAuthenticationSuccess() throws AuthenticationException, JsonProcessingException {
    // given
    String email = "test@example.com";
    String password = "password";
    SigninRequest signinRequest = new SigninRequest(email, password);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent(
        // signinRequest 객체를 json형식으로 설정
        objectMapper.writeValueAsString(signinRequest).getBytes(StandardCharsets.UTF_8));

    MockHttpServletResponse response = new MockHttpServletResponse();

    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email,
        password);

    // authenticationManager.authenticate() 매개변수에 UsernamePasswordAuthenticationToken 타입의 어떤 값이 와도 리턴값은 authToken이 되도록 모킹함.
    when(authenticationManager.authenticate(
        any(UsernamePasswordAuthenticationToken.class))).thenReturn(authToken);

    // when
    Authentication result = signinFilter.attemptAuthentication(request, response);

    //then
    assertEquals(authToken, result);
  }

  @DisplayName("로그인시 회원 인증에 실패한다.")
  @Test
  void attemptAuthenticationFailure() {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setContent("Invalid json".getBytes());

    MockHttpServletResponse response = new MockHttpServletResponse();

    // when & then
    assertThrows(RuntimeException.class,
        () -> signinFilter.attemptAuthentication(request, response));
  }

  @DisplayName("로그인 인증 성공 후 토큰을 생성한다.")
  @Test
  void successfulAuthentication() throws ServletException, IOException {
    // given
    String email = "test@example.com";
    Long memberId = 1L;
    String role = Role.BASIC.name();
    String accessToken = "access-token";
    String refreshToken = "refresh-token";

    when(jwtUtil.createAccessToken(email, memberId, role)).thenReturn(accessToken);
    when(jwtUtil.createRefreshToken(email, memberId, role)).thenReturn(refreshToken);

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();
    Collection<? extends GrantedAuthority> roles = List.of(
        new SimpleGrantedAuthority(Role.BASIC.name()));
    Authentication authResult = new UsernamePasswordAuthenticationToken(email, null, roles);

    // when
    signinFilter.successfulAuthentication(request, response, chain, authResult);

    // then
    verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    assertEquals(accessToken, response.getHeader("Access"));
    Cookie cookie = response.getCookie("Refresh");
    assert cookie != null; // 널이 아님을 확인
    assertEquals(refreshToken, cookie.getValue());
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @DisplayName("로그인 인증 실패 후 401 예외가 발생한다.")
  @Test
  void unsuccessfulAuthentication() throws ServletException, IOException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    org.springframework.security.core.AuthenticationException failed = mock(
        org.springframework.security.core.AuthenticationException.class);

    // when
    signinFilter.unsuccessfulAuthentication(request, response, failed);

    // then
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }
}