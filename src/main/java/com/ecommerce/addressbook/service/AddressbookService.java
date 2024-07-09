package com.ecommerce.addressbook.service;

import com.ecommerce.addressbook.dto.AddressbookRequest;
import com.ecommerce.addressbook.entity.Addressbook;
import com.ecommerce.addressbook.repository.AddressbookRepository;
import com.ecommerce.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    return addressbookRepository.findByMemberIdAndDefaultValue(memberId, 1);
  }

  public Addressbook save(Member member, AddressbookRequest addressbookRequest) {
    return addressbookRepository.save(Addressbook.builder()
        .member(member)
        .name(addressbookRequest.getName())
        .address(addressbookRequest.getAddress())
        .phone(addressbookRequest.getPhone())
        .defaultValue(addressbookRequest.getDefaultValue())
        .build()
    );
  }

}
