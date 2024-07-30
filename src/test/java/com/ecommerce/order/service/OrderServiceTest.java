package com.ecommerce.order.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecommerce.order.dto.OrderDetailRequest;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderDetail;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ProductService productService;

  @InjectMocks
  private OrderService orderService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @DisplayName("주문 정보 저장을 성공한다.")
  @Test
  void save() {
    // given
    Long memberId = 1L;
    Product product = Product.builder().id(1L).build();
    OrderDetailRequest orderDetailRequest = new OrderDetailRequest(1L, 1, OrderStatus.PENDING);
    OrderRequest orderRequest = new OrderRequest("테스트", "서울시 강남구", "010-1234-5678",
        List.of(orderDetailRequest));
    List<OrderDetail> orderDetails = List.of(OrderDetail.builder()
        .product(product)
        .quantity(orderDetailRequest.getQuantity())
        .orderStatus(orderDetailRequest.getOrderStatus())
        .build());
    Order order = Order.builder()
        .id(1L)
        .memberId(memberId)
        .name(orderRequest.getName())
        .address(orderRequest.getAddress())
        .phone(orderRequest.getPhone())
        .orderDetails(orderDetails)
        .build();

    when(productService.findById(orderDetailRequest.getProductId())).thenReturn(product);
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    // when
    Long id = orderService.save(memberId, orderRequest);

    // then
    assertEquals(1L, id);
    verify(orderRepository, times(1)).save(any(Order.class));
    verify(productService, times(1)).findById(orderDetailRequest.getProductId());

  }
}