package com.ecommerce.member.controller;

import com.ecommerce.common.ResponseMessage;
import com.ecommerce.member.dto.SigninRequest;
import com.ecommerce.member.service.SigninService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
