package com.ecommerce.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenPair {

  private final String accessToken;
  private final String refreshToken;
}
