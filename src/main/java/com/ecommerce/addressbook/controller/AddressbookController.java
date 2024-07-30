package com.ecommerce.addressbook.controller;

import com.ecommerce.addressbook.dto.AddressbookRequest;
import com.ecommerce.addressbook.entity.Addressbook;
import com.ecommerce.addressbook.service.AddressbookService;
import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.utils.HeaderUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/address-book")
@RequiredArgsConstructor
@Slf4j
public class AddressbookController {

  private final AddressbookService addressbookService;
  private final JwtUtil jwtUtil;

  /**
   * 주문 정보 입력 (기본 배송지 가져오기)
   *
   * @param request
   * @return
   */
  @GetMapping("/default")
  public ResponseEntity<Addressbook> getDefaultAddress(HttpServletRequest request) {
    String accessToken = HeaderUtil.getAccessToken(request);
    Long memberId = jwtUtil.getMemberId(accessToken);
    Addressbook addressbook = addressbookService.findDefaultAddressByMemberId(memberId);
    if (addressbook == null) {
      log.debug("기본 배송지가 없는 회원");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    return ResponseEntity.ok().body(addressbook);
  }

  /**
   * 주소록 추가
   *
   * @param request
   * @param addressbookRequest
   * @return
   */
  @PostMapping("")
  public ResponseEntity<Addressbook> save(HttpServletRequest request,
      @RequestBody AddressbookRequest addressbookRequest) {
    String accessToken = HeaderUtil.getAccessToken(request);
    Long memberId = jwtUtil.getMemberId(accessToken);
    Addressbook addressbook = addressbookService.save(memberId, addressbookRequest);
    return ResponseEntity.ok().body(addressbook);
  }

}
