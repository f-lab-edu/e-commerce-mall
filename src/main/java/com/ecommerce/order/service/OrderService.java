package com.ecommerce.order.service;

import com.ecommerce.member.entity.Member;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderDetail;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductService productService;

  public Long save(Member member, OrderRequest request) {

    Order order = Order.builder()
        .member(member)
        .name(request.getName())
        .address(request.getAddress())
        .phone(request.getPhone())
        .build();

    log.debug("order Details: " + order.getOrderDetails());

    List<OrderDetail> orderDetails = request.getOrderDetailRequests().stream().map(
        orderDetailRequest -> {
          Product product = productService.findById(orderDetailRequest.getProductId());
          return OrderDetail.builder()
              .product(product)
              .order(order)
              .quantity(orderDetailRequest.getQuantity())
              .orderStatus(orderDetailRequest.getOrderStatus())
              .build();
        }
    ).toList();

    order.getOrderDetails().addAll(orderDetails);

    return orderRepository.save(order).getId();
  }
}
