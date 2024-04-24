package com.ecommerce.member.service;

import com.ecommerce.common.ResponseMessage;

public interface SigninService {
    ResponseMessage signin(String email, String password);
    ResponseMessage logout();
}
