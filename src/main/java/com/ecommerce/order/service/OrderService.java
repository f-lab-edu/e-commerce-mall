package com.ecommerce.order.service;

import com.ecommerce.member.entity.Member;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderDetail;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductService productService;

  public Long save(Member member, OrderRequest request) {
    return orderRepository.save(Order.builder()
        .member(member)
        .name(request.getName())
        .address(request.getAddress())
        .phone(request.getPhone())
        .orderDetails(request.getOrderDetailRequests().stream().map(
            orderDetailRequest -> {
              Product product = productService.findById(orderDetailRequest.getProductId());
              return OrderDetail.builder()
                  .product(product)
                  .quantity(orderDetailRequest.getQuantity())
                  .status(orderDetailRequest.getStatus())
                  .build();
            }
        ).collect(Collectors.toList()))
        .build()).getId();
  }
}
