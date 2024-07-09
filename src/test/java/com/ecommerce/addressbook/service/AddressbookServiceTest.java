package com.ecommerce.addressbook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecommerce.addressbook.dto.AddressbookRequest;
import com.ecommerce.addressbook.entity.Addressbook;
import com.ecommerce.addressbook.repository.AddressbookRepository;
import com.ecommerce.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AddressbookServiceTest {

  @Mock
  private AddressbookRepository addressbookRepository;

  @InjectMocks
  private AddressbookService addressbookService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @DisplayName("기본 배송지 조회에 성공한다.")
  @Test
  void findDefaultAddressByMemberId() {
    // given
    Long memberId = 1L;
    Member member = Member.builder()
        .id(1L)
        .build();
    Addressbook addressbook = Addressbook.builder()
        .id(1L)
        .member(member)
        .build();

    when(addressbookRepository.findByMemberIdAndDefaultValue(anyLong(), anyInt()))
        .thenReturn(addressbook);

    // when
    Addressbook result = addressbookService.findDefaultAddressByMemberId(memberId);

    // then
    assertEquals(member, result.getMember());
    assertEquals(1L, result.getId());
  }

  @DisplayName("배송지 등록에 성공한다.")
  @Test
  void save() {
    // given
    AddressbookRequest addressbookRequest = new AddressbookRequest("테스트", "서울시 강남구",
        "010-1234-5678", 1);
    Member member = Member.builder()
        .id(1L)
        .build();
    Addressbook addressbook = Addressbook.builder()
        .id(1L)
        .member(member)
        .name(addressbookRequest.getName())
        .address(addressbookRequest.getAddress())
        .phone(addressbookRequest.getPhone())
        .defaultValue(addressbookRequest.getDefaultValue())
        .build();

    when(addressbookRepository.save(any(Addressbook.class))).thenAnswer(invocation -> {
      Addressbook arg = invocation.getArgument(0);
      return Addressbook.builder()
          .member(arg.getMember())
          .name(arg.getName())
          .address(arg.getAddress())
          .phone(arg.getPhone())
          .defaultValue(arg.getDefaultValue())
          .build();
    });

    // when
    Addressbook result = addressbookService.save(member, addressbookRequest);

    // Verify the result and repository interaction
    verify(addressbookRepository).save(any(Addressbook.class));
    assertEquals(addressbookRequest.getName(), result.getName());
    assertEquals(addressbookRequest.getAddress(), result.getAddress());
    assertEquals(addressbookRequest.getPhone(), result.getPhone());
    assertEquals(addressbookRequest.getDefaultValue(), result.getDefaultValue());
  }
}