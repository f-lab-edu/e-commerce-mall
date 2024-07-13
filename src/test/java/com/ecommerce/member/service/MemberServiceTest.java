package com.ecommerce.member.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ecommerce.member.dto.SignupRequest;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @InjectMocks
  MemberService memberService;

  @Mock
  MemberRepository memberRepository;
  @Mock
  BCryptPasswordEncoder bCryptPasswordEncoder;
  
  @DisplayName("회원가입에 성공한다.")
  @Test
  void save() {
    // given
    SignupRequest request = SignupRequest.builder()
        .email("member1@test.com")
        .password("f_lab16881577")
        .name("테스트1")
        .phone("01011111111")
        .build();

    Member member = Member.builder()
        .id(1L)
        .email(request.getEmail())
        .password("encodedPassword")
        .name(request.getName())
        .phone(request.getPhone())
        .build();
    when(bCryptPasswordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
    when(memberRepository.save(any(Member.class))).thenReturn(member);

    // when
    Long id = memberService.save(request);

    // then
    Assertions.assertEquals(1L, id);
  }

  @DisplayName("회원가입에 실패한다.")
  @Test
  void saveWithDuplicateEmail() {
    // given
    SignupRequest request = SignupRequest.builder()
        .email("member1@test.com")
        .password("f_lab16881577")
        .name("테스트1")
        .phone("01011111111")
        .build();

    when(bCryptPasswordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
    when(memberRepository.save(any(Member.class))).thenThrow(new RuntimeException());

    // when & then
    assertThrows(RuntimeException.class, () -> {
      memberService.save(request);
    });
  }
}