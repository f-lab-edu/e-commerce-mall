package com.ecommerce.addressbook.service;

import com.ecommerce.addressbook.dto.AddressbookRequest;
import com.ecommerce.addressbook.entity.Addressbook;
import com.ecommerce.addressbook.repository.AddressbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressbookService {

  private final AddressbookRepository addressbookRepository;

  /**
   * 기본 배송지 조회
   *
   * @param memberId
   * @return
   */
  public Addressbook findDefaultAddressByMemberId(Long memberId) {
    return addressbookRepository.findByMemberIdAndIsDefault(memberId, 1);
  }

  @Transactional
  public Addressbook save(Long memberId, AddressbookRequest addressbookRequest) {
    if (addressbookRequest.getIsDefault() == 1) {
      resetDefault(memberId);
    }

    return addressbookRepository.save(Addressbook.builder()
        .memberId(memberId)
        .name(addressbookRequest.getName())
        .address(addressbookRequest.getAddress())
        .phone(addressbookRequest.getPhone())
        .isDefault(addressbookRequest.getIsDefault())
        .build()
    );
  }

  /**
   * 기본 배송지 리셋
   *
   * @param memberId
   */
  public void resetDefault(Long memberId) {
    addressbookRepository.resetIsDefault(memberId);
  }

}
