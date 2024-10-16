package com.ecommerce.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProductRequest {

  private String thumbImg;

  public UpdateProductRequest(String thumbImg) {
    this.thumbImg = thumbImg;
  }
}
