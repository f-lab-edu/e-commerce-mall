package com.ecommerce.member.controller;

import com.ecommerce.member.dto.SignupRequest;
import com.ecommerce.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @PostMapping("")
  public ResponseEntity signup(@Valid @RequestBody SignupRequest request) {
    memberService.save(request);
    return new ResponseEntity(HttpStatus.CREATED);
  }
}
