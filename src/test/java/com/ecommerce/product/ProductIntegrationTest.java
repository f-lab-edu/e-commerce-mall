package com.ecommerce.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.common.AbstractRestDocsTests;
import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.member.entity.Role;
import com.ecommerce.product.dto.AddProductRequest;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductDocumentRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

//TODO: Mock 객체가 아닌 실제 서버로 통합 테스트 코드 수정
@SpringBootTest
@AutoConfigureMockMvc
class ProductIntegrationTest extends AbstractRestDocsTests {

  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private ProductDocumentRepository productDocumentRepository;
  @Autowired
  private JwtUtil jwtUtil;

  private Category category;

  @BeforeEach
  void setUp() {

    productRepository.deleteAll();

    category = Category.builder()
        .id(1L)
        .name("Test Category")
        .build();
    categoryRepository.save(category);
  }

  @DisplayName("상품 등록에 성공한다.")
  @Test
  void addProduct() throws Exception {
    // given
    AddProductRequest request = new AddProductRequest(
        1L,
        "Test Product",
        BigDecimal.valueOf(10000.00),
        "thumb.jpg",
        "detail.jpg",
        "Test Brand",
        10,
        2500,
        0
    );
    String accessToken = jwtUtil.createAccessToken("test@example.com", 1L, Role.ADMIN.name());

    // when
    mockMvc.perform(post("/products")
            .header("Access", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request)))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(request.getName()))
        .andExpect(jsonPath("$.price").value(request.getPrice()))
        .andExpect(jsonPath("$.thumbImg").value(request.getThumbImg()))
        .andExpect(jsonPath("$.detailImg").value(request.getDetailImg()))
        .andExpect(jsonPath("$.brand").value(request.getBrand()))
        .andExpect(jsonPath("$.stock").value(request.getStock()))
        .andExpect(jsonPath("$.deliveryFee").value(request.getDeliveryFee()))
        .andExpect(jsonPath("$.fastDelivery").value(request.getFastDelivery()))
        // REST Docs
        .andDo(restDocs.document(
            requestFields(               // 요청 필드 설명
                fieldWithPath("categoryId").description("상품의 카테고리 ID"),
                fieldWithPath("name").description("상품의 이름"),
                fieldWithPath("price").description("상품의 가격"),
                fieldWithPath("thumbImg").description("상품 썸네일 이미지"),
                fieldWithPath("detailImg").description("상품 상세 이미지"),
                fieldWithPath("brand").description("상품의 브랜드"),
                fieldWithPath("stock").description("상품의 재고 수량"),
                fieldWithPath("deliveryFee").description("배송비"),
                fieldWithPath("fastDelivery").description("신속 배송 여부 (0: 불가, 1: 가능)")
                    .type(JsonFieldType.NUMBER)
                    .attributes(key("constraints").value("0 또는 1만 입력 가능합니다."))
            ),
            responseFields(
                fieldWithPath("id").description("상품 ID"),
                fieldWithPath("name").description("상품의 이름"),
                fieldWithPath("price").description("상품의 가격"),
                fieldWithPath("thumbImg").description("상품 썸네일 이미지"),
                fieldWithPath("detailImg").description("상품 상세 이미지"),
                fieldWithPath("brand").description("상품의 브랜드"),
                fieldWithPath("stock").description("상품의 재고 수량"),
                fieldWithPath("deliveryFee").description("배송비"),
                fieldWithPath("fastDelivery").description("신속 배송 여부 (0: 불가, 1: 가능)")
                    .type(JsonFieldType.NUMBER)
                    .attributes(key("constraints").value("0 또는 1만 입력 가능합니다.")),
                fieldWithPath("createdAt").description("등록 일자 (형식: yyyy-MM-dd'T'HH:mm:ss.SSS)")
                    .type(JsonFieldType.STRING)
                    .attributes(key("format").value("ISO 8601 형식")),
                fieldWithPath("updatedAt").description("최신 업데이트 일자 (형식: yyyy-MM-dd'T'HH:mm:ss.SSS)")
                    .type(JsonFieldType.STRING)
                    .attributes(key("format").value("ISO 8601 형식")),
                fieldWithPath("score").description("인기순 점수").type(JsonFieldType.NUMBER).optional()
            )
        ))
        .andDo(print()); // 로그 출력

    // DB
    List<Product> products = productRepository.findAll();
    assertEquals(1, products.size());
    assertEquals(products.get(0).getCategory().getId(), request.getCategoryId());
    assertEquals(products.get(0).getName(), request.getName());
    assertEquals(0, products.get(0).getPrice().compareTo(request.getPrice()));
    assertEquals(products.get(0).getThumbImg(), request.getThumbImg());
    assertEquals(products.get(0).getDetailImg(), request.getDetailImg());
    assertEquals(products.get(0).getBrand(), request.getBrand());
    assertEquals(products.get(0).getStock(), request.getStock());
    assertEquals(products.get(0).getDeliveryFee(), request.getDeliveryFee());
    assertEquals(products.get(0).getFastDelivery(), request.getFastDelivery());
  }

  @DisplayName("상품 썸네일 변경에 성공한다.")
  @Test
  void updateThumbImg() throws Exception {
    // given
    String oldThumbImg = "old_thumb_img_url";
    String newThumbImg = "new_thumb_img_url";
    Product product = Product.builder()
        .category(category)
        .name("Test Product")
        .price(BigDecimal.valueOf(100))
        .thumbImg(oldThumbImg)
        .detailImg("detail_img_url")
        .brand("TestBrand")
        .stock(10)
        .deliveryFee(0)
        .fastDelivery(0)
        .build();
    Product savedProduct = productRepository.save(product);
    String accessToken = jwtUtil.createAccessToken("test@example.com", 1L, Role.ADMIN.name());

    // when
    mockMvc.perform(patch("/products/{id}", savedProduct.getId())
            .header("Access", accessToken)
            .contentType(MediaType.TEXT_PLAIN_VALUE)
            .content(newThumbImg))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.thumbImg").value(newThumbImg))
        .andDo(restDocs.document(
            pathParameters(
                parameterWithName("id").description("상품 ID")
            ),
//            requestFields(
//                fieldWithPath("thumbImg").description("변경 될 상품 썸네일 이미지")
//            ),
            responseFields(
                fieldWithPath("id").description("상품 ID"),
                fieldWithPath("name").description("상품의 이름"),
                fieldWithPath("price").description("상품의 가격"),
                fieldWithPath("thumbImg").description("변경된 상품 썸네일 이미지"),
                fieldWithPath("detailImg").description("상품 상세 이미지"),
                fieldWithPath("brand").description("상품의 브랜드"),
                fieldWithPath("stock").description("상품의 재고 수량"),
                fieldWithPath("deliveryFee").description("배송비"),
                fieldWithPath("fastDelivery").description("신속 배송 여부 (0: 불가, 1: 가능)")
                    .type(JsonFieldType.NUMBER)
                    .attributes(key("constraints").value("0 또는 1만 입력 가능합니다.")),
                fieldWithPath("createdAt").description("등록 일자 (형식: yyyy-MM-dd'T'HH:mm:ss.SSS)")
                    .type(JsonFieldType.STRING)
                    .attributes(key("format").value("ISO 8601 형식")),
                fieldWithPath("updatedAt").description("최신 업데이트 일자 (형식: yyyy-MM-dd'T'HH:mm:ss.SSS)")
                    .type(JsonFieldType.STRING)
                    .attributes(key("format").value("ISO 8601 형식")),
                fieldWithPath("score").description("인기순 점수").type(JsonFieldType.NUMBER).optional()
            )
        ))
        .andDo(print());
  }
}