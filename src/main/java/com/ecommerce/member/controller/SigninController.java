package com.ecommerce.member.controller;

import com.ecommerce.common.ResponseMessage;
import com.ecommerce.member.dto.SigninRequest;
import com.ecommerce.member.service.SigninService;
import com.ecommerce.utils.SessionUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SigninController {

    private final SigninService signinService;

    @PostMapping("/signin")
    public ResponseMessage signin(@Valid @RequestBody SigninRequest request) {
        ResponseMessage response = signinService.signin(request.getEmail(), request.getPassword());
        return response;
    }

    @GetMapping("/logout")
    public void logout(HttpSession session) {
        SessionUtil.logoutMemberId(session);
    }
}
