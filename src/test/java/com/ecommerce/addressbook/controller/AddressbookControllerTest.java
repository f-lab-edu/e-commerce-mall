package com.ecommerce.addressbook.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.addressbook.dto.AddressbookRequest;
import com.ecommerce.addressbook.entity.Addressbook;
import com.ecommerce.addressbook.service.AddressbookService;
import com.ecommerce.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AddressbookControllerTest {

  @Mock
  private AddressbookService addressbookService;

  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private AddressbookController addressbookController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(addressbookController).build();
    objectMapper = new ObjectMapper();
  }

  @DisplayName("기본 배송지 요청에 성공한다.")
  @Test
  void getDefaultAddress() throws Exception {
    // given
    Long memberId = 1L;
    Addressbook addressbook = Addressbook.builder()
        .id(1L)
        .memberId(memberId)
        .build();

    when(jwtUtil.getMemberId(any())).thenReturn(memberId);
    when(addressbookService.findDefaultAddressByMemberId(memberId)).thenReturn(addressbook);

    // When
    ResultActions resultActions = mockMvc.perform(get("/address-book/default"));

    // Then
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(addressbook.getId()))
        .andExpect(jsonPath("$.memberId").value(addressbook.getMemberId()));
  }

  @DisplayName("기본 배송지가 없어 요청에 실패한다.")
  @Test
  void getDefaultAddressFailure() throws Exception {
    // given
    Long memberId = 1L;
    Addressbook addressbook = Addressbook.builder()
        .id(1L)
        .memberId(memberId)
        .build();

    when(jwtUtil.getMemberId(any())).thenReturn(memberId);
    when(addressbookService.findDefaultAddressByMemberId(memberId)).thenReturn(null);

    // When
    ResultActions resultActions = mockMvc.perform(get("/address-book/default"));

    // Then
    resultActions.andExpect(status().isNotFound());
  }

  @DisplayName("주소록 추가에 성공한다.")
  @Test
  void save() throws Exception {
    // given
    AddressbookRequest addressbookRequest = new AddressbookRequest("테스트", "서울시 강남구",
        "010-1234-5678", 1);
    Long memberId = 1L;
    Addressbook addressbook = Addressbook.builder()
        .id(1L)
        .memberId(memberId)
        .name(addressbookRequest.getName())
        .address(addressbookRequest.getAddress())
        .phone(addressbookRequest.getPhone())
        .isDefault(addressbookRequest.getIsDefault())
        .build();

    when(jwtUtil.getMemberId(any())).thenReturn(memberId);
    when(addressbookService.save(any(Long.class), any(AddressbookRequest.class)))
        .thenReturn(addressbook);

    // when
    ResultActions resultActions = mockMvc.perform(post("/address-book")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(addressbookRequest)));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(addressbook.getId()))
        .andExpect(jsonPath("$.memberId").value(addressbook.getMemberId()))
        .andExpect(jsonPath("$.name").value(addressbook.getName()))
        .andExpect(jsonPath("$.address").value(addressbook.getAddress()))
        .andExpect(jsonPath("$.phone").value(addressbook.getPhone()))
        .andExpect(jsonPath("$.isDefault").value(addressbook.getIsDefault()));
  }
}