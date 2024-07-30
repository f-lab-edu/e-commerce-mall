package com.ecommerce.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.member.dto.SignupRequest;
import com.ecommerce.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MemberService memberService;

  @Autowired
  private ObjectMapper objectMapper;

  @DisplayName("회원가입에 성공한다.")
  @Test
  void signup() throws Exception {
    // given
    SignupRequest request = SignupRequest.builder()
        .email("member1@test.com")
        .password("f_lab16881577")
        .name("테스트1")
        .phone("01011111111")
        .build();

    when(memberService.save(any(SignupRequest.class))).thenReturn(1L);

    // when & then
    mockMvc.perform(post("/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("1"));
  }

  @DisplayName("올바르지 않은 이메일 요청으로 회원가입에 실패한다.")
  @Test
  void signupWithInvalidEmail() throws Exception {
    // given
    SignupRequest request = SignupRequest.builder()
        .email("invalid-email")
        .password("f_lab16881577")
        .name("테스트1")
        .phone("01011111111")
        .build();

    // when & then
    mockMvc.perform(post("/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").value("올바른 이메일 형식을 입력하세요"));
  }

  @DisplayName("중복된 이메일로 회원가입에 실패한다.")
  @Test
  void signupDuplicateEmail() throws Exception {
    // given
    SignupRequest request = SignupRequest.builder()
        .email("member1@test.com")
        .password("f_lab16881577")
        .name("테스트1")
        .phone("01011111111")
        .build();

    when(memberService.save(any(SignupRequest.class))).thenThrow(new RuntimeException());

    // when & then
    mockMvc.perform(post("/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError());
  }
}