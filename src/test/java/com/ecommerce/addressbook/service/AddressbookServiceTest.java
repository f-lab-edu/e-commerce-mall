package com.ecommerce.addressbook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecommerce.addressbook.dto.AddressbookRequest;
import com.ecommerce.addressbook.entity.Addressbook;
import com.ecommerce.addressbook.repository.AddressbookRepository;
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
    Addressbook addressbook = Addressbook.builder()
        .id(1L)
        .memberId(memberId)
        .build();

    when(addressbookRepository.findByMemberIdAndIsDefault(anyLong(), anyInt()))
        .thenReturn(addressbook);

    // when
    Addressbook result = addressbookService.findDefaultAddressByMemberId(memberId);

    // then
    assertEquals(memberId, result.getMemberId());
    assertEquals(1L, result.getId());
  }

  @DisplayName("기본 배송지 설정한 새로운 배송지 등록에 성공한다.")
  @Test
  void saveIsDefault() {
    // given
    AddressbookRequest addressbookRequest = new AddressbookRequest("테스트", "서울시 강남구",
        "010-1234-5678", 1);
    Long memberId = 1L;

    when(addressbookRepository.save(any(Addressbook.class))).thenAnswer(invocation -> {
      Addressbook arg = invocation.getArgument(0);
      return Addressbook.builder()
          .memberId(arg.getMemberId())
          .name(arg.getName())
          .address(arg.getAddress())
          .phone(arg.getPhone())
          .isDefault(arg.getIsDefault())
          .build();
    });

    // spy를 사용하면 실제 메서드를 호출하면서도 특정 메서드의 호출 여부를 검증
    AddressbookService spyAddressbookService = spy(addressbookService);

    // when
    Addressbook result = spyAddressbookService.save(memberId, addressbookRequest);

    // Verify the result and repository interaction
    verify(spyAddressbookService, times(1)).resetDefault(memberId);
    verify(addressbookRepository, times(1)).save(any(Addressbook.class));
    assertEquals(addressbookRequest.getName(), result.getName());
    assertEquals(addressbookRequest.getAddress(), result.getAddress());
    assertEquals(addressbookRequest.getPhone(), result.getPhone());
    assertEquals(addressbookRequest.getIsDefault(), result.getIsDefault());
  }

  @DisplayName("기본 배송지 설정 하지 않은 새로운 배송지 등록에 성공한다.")
  @Test
  void saveIsNotDefault() {
    // given
    AddressbookRequest addressbookRequest = new AddressbookRequest("테스트", "서울시 강남구",
        "010-1234-5678", 0);
    Long memberId = 1L;

    when(addressbookRepository.save(any(Addressbook.class))).thenAnswer(invocation -> {
      Addressbook arg = invocation.getArgument(0);
      return Addressbook.builder()
          .memberId(arg.getMemberId())
          .name(arg.getName())
          .address(arg.getAddress())
          .phone(arg.getPhone())
          .isDefault(arg.getIsDefault())
          .build();
    });

    // spy를 사용하면 실제 메서드를 호출하면서도 특정 메서드의 호출 여부를 검증
    AddressbookService spyAddressbookService = spy(addressbookService);

    // when
    Addressbook result = spyAddressbookService.save(memberId, addressbookRequest);

    // Verify the result and repository interaction
    verify(spyAddressbookService, never()).resetDefault(any());
    verify(addressbookRepository, times(1)).save(any(Addressbook.class));
    assertEquals(addressbookRequest.getName(), result.getName());
    assertEquals(addressbookRequest.getAddress(), result.getAddress());
    assertEquals(addressbookRequest.getPhone(), result.getPhone());
    assertEquals(addressbookRequest.getIsDefault(), result.getIsDefault());
  }

  @DisplayName("기본 배송지 리셋에 성공한다.")
  @Test
  void resetDefault() {
    // given
    Long memberId = 1L;

    // when
    addressbookService.resetDefault(memberId);

    // then
    verify(addressbookRepository).resetIsDefault(anyLong());

  }
}