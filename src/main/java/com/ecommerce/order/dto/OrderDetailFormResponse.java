package com.ecommerce.order.dto;

import com.ecommerce.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OrderDetailFormResponse {

  private Product product;
  private int quantity;

}
