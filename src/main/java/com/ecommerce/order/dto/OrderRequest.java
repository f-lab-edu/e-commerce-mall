package com.ecommerce.order.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class OrderRequest {
  
  private String name;
  private String address;
  private String phone;
  private List<OrderDetailRequest> orderDetailRequests;
}
