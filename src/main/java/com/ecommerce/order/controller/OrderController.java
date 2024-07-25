package com.ecommerce.order.controller;

import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.order.dto.OrderDetailFormRequest;
import com.ecommerce.order.dto.OrderDetailFormResponse;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

  private final OrderService orderService;
  private final ProductService productService;
  private final JwtUtil jwtUtil;

  /**
   * 주문 하기
   *
   * @param request
   * @param orderRequest
   * @return
   */
  @PostMapping("")
  public ResponseEntity<Long> order(HttpServletRequest request,
      @RequestBody OrderRequest orderRequest) {
    String accessToken = request.getHeader("Access");
    Long memberId = jwtUtil.getMemberId(accessToken);
    Long id = orderService.save(memberId, orderRequest);
    return ResponseEntity.ok().body(id);
  }

  /**
   * 주문 정보 입력 (주문 상품 정보 가져오기)
   *
   * @param request
   * @return ResponseEntity<OrderFormResponse>
   */
  @GetMapping("/form")
  public ResponseEntity<List<OrderDetailFormResponse>> orderForm(
      @RequestBody OrderDetailFormRequest request) {

    List<OrderDetailFormResponse> responses = request.getOrderDetails().stream()
        .map(
            orderDetail -> {
              Product product = productService.findById(orderDetail.getProductId());
              return OrderDetailFormResponse.builder()
                  .product(product)
                  .quantity(orderDetail.getQuantity())
                  .build();
            })
        .collect(Collectors.toList());

    return ResponseEntity.ok().body(responses);
  }
}
