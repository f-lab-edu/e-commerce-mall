package com.ecommerce.order.dto;

import com.ecommerce.order.entity.Status;
import lombok.Getter;

@Getter
public class OrderDetailRequest {

  private Long productId;
  private int quantity;
  private Status status;
}
