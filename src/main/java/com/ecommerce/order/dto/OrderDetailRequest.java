package com.ecommerce.order.dto;

import com.ecommerce.order.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDetailRequest {

  private Long productId;
  private int quantity;
  private Status status;
}
