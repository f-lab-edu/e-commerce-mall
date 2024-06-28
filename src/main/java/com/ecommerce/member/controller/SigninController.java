package com.ecommerce.member.controller;

import com.ecommerce.common.ResponseMessage;
import com.ecommerce.member.dto.SigninRequest;
import com.ecommerce.member.service.SigninService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@Deprecated
@RestController
@RequiredArgsConstructor
public class SigninController {

  private final SigninService signinService;

  @PostMapping("/sign-in")
  public ResponseMessage signin(@Valid @RequestBody SigninRequest request) {
    return signinService.signin(request.getEmail(), request.getPassword());
  }

  @GetMapping("/logout")
  public ResponseMessage logout(HttpSession session) {
    return signinService.logout();
  }
}
