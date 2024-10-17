package com.ecommerce.member.controller;

import com.ecommerce.common.ResponseMessage;
import com.ecommerce.member.dto.SignupRequest;
import com.ecommerce.member.service.MemberService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
  public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      // 유효성 검증 실패 시 처리
      Map<String, String> errors = new HashMap<>();
      for (FieldError error : bindingResult.getFieldErrors()) {
        errors.put(error.getField(), error.getDefaultMessage());
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    try {
      Long id = memberService.save(request);
      return ResponseEntity.ok(ResponseMessage.builder().data(id).build());
    } catch (Exception e) {
      // 서비스에서 예외 발생 시 처리
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
