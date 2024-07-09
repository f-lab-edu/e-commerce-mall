package com.ecommerce.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.member.entity.Member;
import com.ecommerce.order.dto.OrderDetailFormResponse;
import com.ecommerce.order.dto.OrderDetailRequest;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.entity.Status;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import com.ecommerce.utils.MemberUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

  @Mock
  private OrderService orderService;

  @Mock
  private ProductService productService;

  @Mock
  private MemberUtil memberUtil;

  @InjectMocks
  private OrderController orderController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    objectMapper = new ObjectMapper();
  }

  @DisplayName("주문에 성공한다.")
  @Test
  void order() throws Exception {
    // given
    OrderDetailRequest orderDetailRequest = new OrderDetailRequest(1L, 1, Status.PENDING);
    OrderRequest orderRequest = new OrderRequest("테스트", "서울시 강남구", "010-1234-5678",
        List.of(orderDetailRequest));
    Member member = Member.builder()
        .id(1L)
        .build();

    when(memberUtil.getLoginMember(any())).thenReturn(member);
    when(orderService.save(any(Member.class), any(OrderRequest.class))).thenReturn(1L);

    // when
    ResultActions resultActions = mockMvc.perform(post("/orders")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(orderRequest)));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().string("1"));
  }

  @DisplayName("주문 상품 정보 요청에 성공한다.")
  @Test
  void orderForm() throws Exception {
    // given
    Product product = Product.builder().id(1L).build();
    String requests = """
                [{
                    "productId":1,
                    "quantity":2
                }]
        """;
    OrderDetailFormResponse response = OrderDetailFormResponse.builder()
        .product(product)
        .quantity(2)
        .build();
    List<OrderDetailFormResponse> responses = List.of(response);

    when(productService.findById(anyLong())).thenReturn(product);

    // when
    ResultActions resultActions = mockMvc.perform(get("/orders/form")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(requests));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$[0].product.id").value(1L))
        .andExpect(jsonPath("$[0].quantity").value(2));
  }
}