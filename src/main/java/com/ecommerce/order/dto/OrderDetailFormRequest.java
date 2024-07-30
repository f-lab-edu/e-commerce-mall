package com.ecommerce.order.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderDetailFormRequest {

  private final List<OrderDetailRequest> orderDetails = new ArrayList<>();

}
