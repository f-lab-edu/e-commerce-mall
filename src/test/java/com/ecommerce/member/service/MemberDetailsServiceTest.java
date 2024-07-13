package com.ecommerce.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

class MemberDetailsServiceTest {

  @InjectMocks
  private MemberDetailsService memberDetailsService;

  @Mock
  private MemberRepository memberRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this); // Mockito 초기화
  }

  @DisplayName("회원 로드에 성공한다.")
  @Test
  void loadUserByUsername() {
    // Given
    String email = "test@example.com";
    Member member = Member.builder().email("test@example.com").password("password").build();

    when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

    // When
    UserDetails userDetails = memberDetailsService.loadUserByUsername(email);

    // Then
    assertEquals(email, userDetails.getUsername());
    assertEquals(member.getPassword(), userDetails.getPassword());
    verify(memberRepository, times(1)).findByEmail(email);
  }

  @DisplayName("존재하지 않는 회원으로 예외를 반환한다.")
  @Test
  void loadUserByNonExistingUsernameFailure() {
    // Given
    String email = "nonexisting@example.com";

    when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

    // When / Then
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> memberDetailsService.loadUserByUsername(email)
    );
    assertEquals("존재하지 않는 회원입니다.", exception.getMessage());
    verify(memberRepository, times(1)).findByEmail(email);
  }
}