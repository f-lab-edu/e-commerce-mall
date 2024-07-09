package com.ecommerce.order.dto;

import lombok.Getter;

@Getter
public class OrderDetailFormRequest {

  private Long productId;
  private int quantity;

}
