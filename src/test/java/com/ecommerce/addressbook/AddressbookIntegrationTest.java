package com.ecommerce.addressbook;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.addressbook.dto.AddressbookRequest;
import com.ecommerce.addressbook.entity.Addressbook;
import com.ecommerce.addressbook.repository.AddressbookRepository;
import com.ecommerce.common.AbstractRestDocsTests;
import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.entity.Role;
import com.ecommerce.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//TODO: Mock 객체가 아닌 실제 서버로 통합 테스트 코드 수정
public class AddressbookIntegrationTest extends AbstractRestDocsTests {

  @Autowired
  AddressbookRepository addressbookRepository;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private JwtUtil jwtUtil;

  private Member member;


  @BeforeEach
  void setUp() {

    addressbookRepository.deleteAll();
    memberRepository.deleteAll();

    // 사용자 계정 등록
    member = Member.builder()
        .email("member@test.com")
        .password(bCryptPasswordEncoder.encode("f_lab16881577"))
        .name("테스트")
        .phone("01011111111")
        .role(Role.BASIC)
        .build();
    memberRepository.save(member);
  }

  @DisplayName("기본 배송지 조회에 성공한다.")
  @Test
  void getDefaultAddress() throws Exception {

    // given
    String address = "서울시 강남구";
    String phone = "010-1234-5678";
    Addressbook addressbook = Addressbook.builder()
        .memberId(member.getId())
        .name(member.getName())
        .address(address)
        .phone(phone)
        .isDefault(1)
        .build();
    addressbookRepository.save(addressbook);

    String accessToken = jwtUtil.createAccessToken(member.getEmail(), member.getId(),
        member.getRole().name());

    // when
    mockMvc.perform(get("/address-book/default")
            .header("Access", accessToken))

        // then
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(addressbook.getId()))
        .andExpect(jsonPath("$.memberId").value(addressbook.getMemberId()))

        // REST Docs
        .andDo(restDocs.document(
            responseFields(
                fieldWithPath("id").description("기본 주소 ID"),
                fieldWithPath("memberId").description("회원 ID"),
                fieldWithPath("name").description("기본 주소의 받는분 성함").optional(),
                fieldWithPath("address").description("기본 주소지"),
                fieldWithPath("phone").description("기본 주소 연락처"),
                fieldWithPath("isDefault").description("기본 주소 여부"),
                fieldWithPath("createdAt").description("등록 일자"),
                fieldWithPath("updatedAt").description("최신 업데이트 일자")
            )
        ))
        .andDo(print());
  }

  @DisplayName("기본 배송지가 없으면 Not Found를 전송한다.")
  @Test
  void getDefaultAddressFailure() throws Exception {
    // given
    String accessToken = jwtUtil.createAccessToken(member.getEmail(), member.getId(),
        member.getRole().name());

    // When
    mockMvc.perform(get("/address-book/default")
            .header("Access", accessToken))

        // Then
        .andExpect(status().isNotFound());
  }

  @DisplayName("주소록 추가에 성공한다.")
  @Test
  void save() throws Exception {
    // given
    String accessToken = jwtUtil.createAccessToken(member.getEmail(), member.getId(),
        member.getRole().name());
    AddressbookRequest addressbookRequest = new AddressbookRequest("테스트", "서울시 강남구",
        "010-1234-5678", 1);

    // when
    mockMvc.perform(post("/address-book")
            .header("Access", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(addressbookRequest)))

        // then
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.memberId").value(member.getId()))
        .andExpect(jsonPath("$.name").value(addressbookRequest.getName()))
        .andExpect(jsonPath("$.address").value(addressbookRequest.getAddress()))
        .andExpect(jsonPath("$.phone").value(addressbookRequest.getPhone()))
        .andExpect(jsonPath("$.isDefault").value(addressbookRequest.getIsDefault()))

        .andDo(restDocs.document(
            requestFields(
                fieldWithPath("name").description("받는분 성함"),
                fieldWithPath("address").description("주소지"),
                fieldWithPath("phone").description("연락처"),
                fieldWithPath("isDefault").description("기본 주소지 설정 여부(설정 시 기존 기본 주소지와 자동 변경됨)")
            ),
            responseFields(
                fieldWithPath("id").description("주소지 ID"),
                fieldWithPath("memberId").description("회원 ID"),
                fieldWithPath("name").description("받는분 성함"),
                fieldWithPath("address").description("주소지"),
                fieldWithPath("phone").description("연락처"),
                fieldWithPath("isDefault").description("기본 주소지 설정 여부"),
                fieldWithPath("createdAt").description("등록 일자"),
                fieldWithPath("updatedAt").description("최신 업데이트 일자")
            )
        )).andDo(print());
  }
}
