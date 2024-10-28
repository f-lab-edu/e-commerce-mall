package com.ecommerce.order;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.addressbook.repository.AddressbookRepository;
import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.common.AbstractRestDocsTests;
import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.entity.Role;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.order.dto.OrderDetailFormRequest;
import com.ecommerce.order.dto.OrderDetailRequest;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class OrderIntegrationTest extends AbstractRestDocsTests {

  @Autowired
  OrderRepository orderRepository;

  @Autowired
  AddressbookRepository addressbookRepository;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private JwtUtil jwtUtil;

  private Member member;
  private Product product;

  @BeforeEach
  void setUp() {

    orderRepository.deleteAll();
    addressbookRepository.deleteAll();
    memberRepository.deleteAll();
    productRepository.deleteAll();

    // 사용자 계정 등록
    member = Member.builder()
        .email("member@test.com")
        .password(bCryptPasswordEncoder.encode("f_lab16881577"))
        .name("테스트")
        .phone("01011111111")
        .role(Role.BASIC)
        .build();
    memberRepository.save(member);

    // 상품 등록
    Category category = Category.builder()
        .id(1L)
        .name("Test Category")
        .build();
    categoryRepository.save(category);

    product = Product.builder()
        .category(category)
        .name("Test Product")
        .price(BigDecimal.valueOf(10000.00))
        .thumbImg("thumb.jpg")
        .detailImg("detail.ipg")
        .brand("Test Brand")
        .stock(10)
        .deliveryFee(2500)
        .fastDelivery(0)
        .build();
    productRepository.save(product);
  }

  @DisplayName("주문 처리에 성공 한다")
  @Test
  void order() throws Exception {
    // given
    String accessToken = jwtUtil.createAccessToken(member.getEmail(), member.getId(),
        member.getRole().name());

    OrderDetailRequest orderDetailRequest = new OrderDetailRequest(product.getId(), 1,
        OrderStatus.PENDING);
    OrderRequest orderRequest = new OrderRequest("테스트", "서울시 강남구", "010-1234-5678",
        List.of(orderDetailRequest));

    // when
    mockMvc.perform(post("/orders").header("Access", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(orderRequest)))

        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(1L))

        // REST Docs
        .andDo(restDocs.document(
            requestFields(
                fieldWithPath("name").description("받는 분 성함"),
                fieldWithPath("address").description("배송지"),
                fieldWithPath("phone").description("연락처"),
                fieldWithPath("orderDetailRequests[]").description("주문 상품 목록"),
                fieldWithPath("orderDetailRequests[].productId").description("상품 ID"),
                fieldWithPath("orderDetailRequests[].quantity").description("주문 수량"),
                fieldWithPath("orderDetailRequests[].orderStatus").description("주문 상태")
            ),
            responseFields(
                fieldWithPath("status").description("응답 상태 메시지"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("data").description("주문 고유 ID")
            )
        )).andDo(print());
  }

  @DisplayName("주문 정보 입력 전, 주문할 상품 정보를 가져온다.")
  @Test
  void orderForm() throws Exception {
    // given
    String accessToken = jwtUtil.createAccessToken(member.getEmail(), member.getId(),
        member.getRole().name());

    OrderDetailRequest orderDetailRequest = new OrderDetailRequest(product.getId(), 2,
        OrderStatus.PENDING);
    OrderDetailFormRequest orderDetailFormRequest = new OrderDetailFormRequest();
    orderDetailFormRequest.getOrderDetails().add(orderDetailRequest);

    // when
    mockMvc.perform(get("/orders/form").header("Access", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(orderDetailFormRequest)))

        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].product.id").value(product.getId()))
        .andExpect(jsonPath("$[0].quantity").value(orderDetailRequest.getQuantity()))

        // REST Docs
        .andDo(restDocs.document(
            requestFields(
                fieldWithPath("orderDetails").description("주문할 상품 정보"),
                fieldWithPath("orderDetails[].productId").description("상품 ID"),
                fieldWithPath("orderDetails[].quantity").description("주문 수량"),
                fieldWithPath("orderDetails[].orderStatus").description("주문상태:PENDING").optional()
            ),
            responseFields(
                fieldWithPath("[].product").description("상품 정보"),
                fieldWithPath("[].product.id").description("상품 ID"),
                fieldWithPath("[].product.name").description("상품의 이름"),
                fieldWithPath("[].product.price").description("상품의 가격"),
                fieldWithPath("[].product.thumbImg").description("변경된 상품 썸네일 이미지"),
                fieldWithPath("[].product.detailImg").description("상품 상세 이미지"),
                fieldWithPath("[].product.brand").description("상품의 브랜드"),
                fieldWithPath("[].product.stock").description("상품의 재고 수량"),
                fieldWithPath("[].product.deliveryFee").description("배송비"),
                fieldWithPath("[].product.fastDelivery").description("신속 배송 여부 (0: 불가, 1: 가능)")
                    .type(JsonFieldType.NUMBER)
                    .attributes(key("constraints").value("0 또는 1만 입력 가능합니다.")),
                fieldWithPath("[].product.createdAt").description(
                        "등록 일자 (형식: yyyy-MM-dd'T'HH:mm:ss.SSS)")
                    .type(JsonFieldType.STRING)
                    .attributes(key("format").value("ISO 8601 형식")),
                fieldWithPath("[].product.updatedAt").description(
                        "최신 업데이트 일자 (형식: yyyy-MM-dd'T'HH:mm:ss.SSS)")
                    .type(JsonFieldType.STRING)
                    .attributes(key("format").value("ISO 8601 형식")),
                fieldWithPath("[].product.score").description("인기순 점수").type(JsonFieldType.NUMBER)
                    .optional(),
                fieldWithPath("[].quantity").description("수량")
            )
        )).andDo(print());
  }
}
