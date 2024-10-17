package com.ecommerce.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.common.AbstractRestDocsTests;
import com.ecommerce.member.dto.SignupRequest;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

//TODO: Mock 객체가 아닌 실제 서버로 통합 테스트 코드 수정
class MemberIntegrationTest extends AbstractRestDocsTests {

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @BeforeEach
  void setUp() {
    memberRepository.deleteAll();
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
    final ResultActions action = mockMvc.perform(post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    action.andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(1L))
        .andDo(restDocs.document(
            requestFields(
                fieldWithPath("email").description("회원 이메일"),
                fieldWithPath("password").description("회원 비밀번호"),
                fieldWithPath("name").description("회원 이름"),
                fieldWithPath("phone").description("회원 연락처"),
                fieldWithPath("role").description("회원 권한 - [BASIC, ADMIN]").optional()
            ),
            relaxedResponseFields(
                fieldWithPath("data").description("회원 고유 ID")
            )
        ))
        .andDo(print());

    // check DB
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

    // when & then
    mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request2)))
        .andExpect(status().isInternalServerError());
  }
}