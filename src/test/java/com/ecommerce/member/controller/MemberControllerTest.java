package com.ecommerce.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.member.dto.SignupRequest;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  private WebApplicationContext context;
  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @BeforeEach // 각 테스트 케이스 실행 전 실행
  public void mockMvcSetUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build(); // MockMvc 설정
    memberRepository.deleteAll(); // 데이터 초기화
  }

  @DisplayName("회원가입에 성공한다.")
  @Test
  void signup() throws Exception {
    // given
    final String url = "/members";
    final SignupRequest request = SignupRequest.builder()
        .email("member1@test.com")
        .password("f_lab16881577")
        .name("테스트1")
        .phone("01011111111")
        .build();

    // when
    final ResultActions result = mockMvc.perform(post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isOk())
        .andDo(print());

    List<Member> members = memberRepository.findAll();

    assertThat(members.size()).isEqualTo(1);
    assertThat(members.get(0).getEmail()).isEqualTo(request.getEmail());
    assertThat(bCryptPasswordEncoder.matches(request.getPassword(),
        members.get(0).getPassword())).isTrue();
    assertThat(members.get(0).getName()).isEqualTo(request.getName());
    assertThat(members.get(0).getPhone()).isEqualTo(request.getPhone());
  }

  @DisplayName("이메일이 중복되면 회원가입에 실패한다.")
  @Test
  void signUpWithDuplicateEmail() throws Exception {
    // given
    final String url = "/members";
    final SignupRequest request1 = SignupRequest.builder()
        .email("member1@test.com")
        .password("f_lab16881577")
        .name("테스트1")
        .phone("01011111111")
        .build();

    final SignupRequest request2 = SignupRequest.builder()
        .email("member1@test.com")
        .password("f_lab16881577")
        .name("테스트2")
        .phone("01011111111")
        .build();

    mockMvc.perform(post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request1)));

    // when - then
    Assertions.assertThrows(ServletException.class, () -> {
      mockMvc.perform(post(url)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request2)));
    });
  }
}