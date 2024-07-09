package com.ecommerce.order.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderRequest {

  private String name;
  private String address;
  private String phone;
  private List<OrderDetailRequest> orderDetailRequests;
}
