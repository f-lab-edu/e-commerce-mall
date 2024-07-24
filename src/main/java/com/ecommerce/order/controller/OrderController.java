package com.ecommerce.order.controller;

import com.ecommerce.member.entity.Member;
import com.ecommerce.order.dto.OrderDetailFormRequest;
import com.ecommerce.order.dto.OrderDetailFormResponse;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import com.ecommerce.utils.MemberUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
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
  private final MemberUtil memberUtil;

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
    Member member = memberUtil.getLoginMember(request);
    Long id = orderService.save(member, orderRequest);
    return ResponseEntity.ok().body(id);
  }

  /**
   * 주문 정보 입력 (주문 상품 정보 가져오기)
   *
   * @param requests
   * @return ResponseEntity<OrderFormResponse>
   */
  @GetMapping("/form")
  public ResponseEntity<List<OrderDetailFormResponse>> orderForm(
      @RequestBody OrderDetailFormRequest... requests) {

    List<OrderDetailFormResponse> responses = Arrays.stream(requests)
        .map(
            orderFormRequest -> {
              Product product = productService.findById(orderFormRequest.getProductId());
              return OrderDetailFormResponse.builder()
                  .product(product)
                  .quantity(orderFormRequest.getQuantity())
                  .build();
            })
        .collect(Collectors.toList());

    return ResponseEntity.ok().body(responses);
  }
}
